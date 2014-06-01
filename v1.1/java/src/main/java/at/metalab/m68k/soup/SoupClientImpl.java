package at.metalab.m68k.soup;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;
import at.metalab.m68k.soup.resource.User;
import at.metalab.m68k.soup.resource.posts.Event;
import at.metalab.m68k.soup.resource.posts.Image;
import at.metalab.m68k.soup.resource.posts.Link;
import at.metalab.m68k.soup.resource.posts.Quote;
import at.metalab.m68k.soup.resource.posts.Regular;
import at.metalab.m68k.soup.resource.posts.Review;
import at.metalab.m68k.soup.resource.posts.Video;
import at.metalab.m68k.soup.scribe.LocalCallbackVerifier;
import at.metalab.m68k.soup.scribe.SoupApi;

/**
 * @author m68k
 * 
 */
public class SoupClientImpl implements SoupClient {

	private final OAuthService service;

	private Token accessToken;

	private int localPort;

	private class ErrorCodes {
		private final static int NOT_AUTHORIZED = 401;

		private final static int INTERNAL_SERVER_ERROR = 500;
	}

	public SoupClientImpl(Properties soupApiProperties,
			Properties accessTokenProperties, int localPort) {
		String accesstokenToken = accessTokenProperties.getProperty(
				OAuthHelper.SOUP_ACCESSTOKEN_TOKEN, "");
		String accesstokenSecret = accessTokenProperties.getProperty(
				OAuthHelper.SOUP_ACCESSTOKEN_SECRET, "");

		this.accessToken = new Token(accesstokenToken, accesstokenSecret);
		this.localPort = localPort;

		String apiKey = soupApiProperties.getProperty("soup.api.key");
		String apiSecret = soupApiProperties.getProperty("soup.api.secret");

		this.service = new ServiceBuilder().provider(SoupApi.class)
				.apiKey(apiKey).apiSecret(apiSecret)
				.callback(String.format("http://127.0.0.1:%d", localPort))
				.build();
	}

	public synchronized Properties authenticate() throws NotAuthorizedException {
		Verifier verifier = null;
		Token requestToken = null;

		try {
			// Instantiate the verifier first to have the server socket already
			// running
			verifier = new LocalCallbackVerifier(localPort);

			requestToken = service.getRequestToken();

			// Open the browser to ask for permission
			askForUserPermission(service, requestToken);
		} catch (Exception exception) {
			throw new RuntimeException("Could not start authentication",
					exception);
		}

		// This will block until the user has granted or denied permission
		try {
			accessToken = service.getAccessToken(requestToken, verifier);
		} catch (NotAuthorizedException notAuthorizedException) {
			// User clicked on deny
			throw notAuthorizedException;
		}

		return OAuthHelper.createAccessTokenProperties(accessToken);
	}

	public Token getAccessToken() {
		return accessToken;
	}

	private Response send(OAuthRequest request) {
		service.signRequest(accessToken, request);
		return request.send();
	}

	public User getUser() throws NotAuthorizedException {
		OAuthRequest request = new OAuthRequest(Verb.GET,
				soupApi("/authenticate"));
		Response response = send(request);

		if (response.getCode() == ErrorCodes.NOT_AUTHORIZED
				|| response.getCode() == ErrorCodes.INTERNAL_SERVER_ERROR) {
			throw new NotAuthorizedException();
		}

		try {
			User user = User
					.create(OM.readTree(response.getBody()).get("user"));

			return user;
		} catch (Exception exception) {
			throw new RuntimeException("could not parse response", exception);
		}
	}

	private final static ObjectMapper OM = new ObjectMapper();

	private abstract class PostTemplate {

		protected abstract void buildPostNode(ObjectNode postNode)
				throws IOException, JsonMappingException, JsonParseException;

		protected PostResult createPostResult(Response response)
				throws IOException, JsonMappingException, JsonParseException {
			JsonNode rootNode = OM.readTree(response.getBody());
			JsonNode postNode = rootNode.get("post");

			PostResult p = new PostResult();
			p.setBlogId(postNode.get("blog_id").getLongValue());
			p.setId(postNode.get("id").getLongValue());
			p.setCreatedAt(postNode.get("created_at").getTextValue());
			p.setUpdatedAt(postNode.get("updated_at").getTextValue());

			return p;
		}

		public PostResult post(String url) throws NotAuthorizedException {
			try {
				ObjectNode rootNode = OM.createObjectNode();

				ObjectNode postNode = OM.createObjectNode();
				rootNode.put("post", postNode);

				buildPostNode(postNode);

				String body = OM.writerWithDefaultPrettyPrinter()
						.writeValueAsString(rootNode);
				OAuthRequest post = createRequest(Verb.POST, url, body);
				Response response = send(post);

				if (response.getCode() == ErrorCodes.NOT_AUTHORIZED) {
					throw new NotAuthorizedException();
				}

				return createPostResult(response);
			} catch (IOException ioException) {
				throw new RuntimeException("", ioException);
			}
		}
	}

	public PostResult post(Blog blog, final Regular post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("title", post.getTitle());
				postNode.put("body", post.getBody());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/regular"));
	}

	public PostResult post(Blog blog, final Link post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("url", post.getUrl());
				postNode.put("description", post.getDescription());
				postNode.put("caption", post.getCaption());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/links"));
	}

	public PostResult post(Blog blog, final Review post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("url", post.getUrl());
				postNode.put("rating", post.getRating());
				postNode.put("review", post.getReview());
				postNode.put("title", post.getTitle());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/reviews"));
	}

	public PostResult post(Blog blog, final Event post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("location", post.getLocation());
				postNode.put("description", post.getDescription());
				postNode.put("start_date", formatRfc822(post.getStartDate()));
				postNode.put("end_date", formatRfc822(post.getEndDate()));
				postNode.put("title", post.getTitle());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/events"));
	}

	public PostResult post(Blog blog, final Image post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("url", post.getUrl());
				postNode.put("description", post.getDescription());
				postNode.put("source", post.getSource());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/images"));
	}

	public PostResult post(Blog blog, final Quote post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("quote", post.getQuote());
				postNode.put("source", post.getSource());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/quotes"));
	}

	public PostResult post(Blog blog, final Video post)
			throws NotAuthorizedException {
		return new PostTemplate() {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("url", post.getUrl());
				postNode.put("embed-code", post.getEmbedCode());
				postNode.put("description", post.getDescription());
				postNode.put("tags", post.getTags());
			}
		}.post(blog.getResource().concat("/posts/videos"));
	}

	private static OAuthRequest createRequest(Verb verb, String url, String body) {
		OAuthRequest post = new OAuthRequest(verb, url);
		post.addHeader("Content-Type", "application/json");
		post.addPayload(body);

		return post;
	}

	/**
	 * Open the default browser to ask for permission to access the soup
	 * account.
	 * 
	 * @param oAuthService
	 * @param requestToken
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private static void askForUserPermission(OAuthService oAuthService,
			Token requestToken) throws IOException, URISyntaxException {
		if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(
					new URI(String.format("%s?%s",
							oAuthService.getAuthorizationUrl(requestToken),
							requestToken.getRawResponse())));
		} else {
			throw new RuntimeException(
					"Could not open browser for authorization");
		}
	}

	/**
	 * Returns the resource prefixed with the Soup API baseUrl.
	 * 
	 * @param resource
	 * @return
	 */
	private static String soupApi(String resource) {
		return String.format("%s%s", "https://api.soup.io/api/v1.1", resource);
	}

	private static String formatRfc822(Calendar calendar) {
		return new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z", Locale.US)
				.format(calendar.getTime());
	}

}
