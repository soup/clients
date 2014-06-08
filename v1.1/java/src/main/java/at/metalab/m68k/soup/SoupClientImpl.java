package at.metalab.m68k.soup;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.node.ObjectNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import at.metalab.m68k.soup.http.SoupRequest;
import at.metalab.m68k.soup.http.requests.BodylessJsonRequestImpl;
import at.metalab.m68k.soup.http.requests.JsonRequestImpl;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.Group;
import at.metalab.m68k.soup.resource.PostResult;
import at.metalab.m68k.soup.resource.User;
import at.metalab.m68k.soup.resource.posts.Postable;
import at.metalab.m68k.soup.scribe.LocalCallbackVerifier;
import at.metalab.m68k.soup.scribe.SoupApi;

/**
 * @author m68k
 * 
 */
public class SoupClientImpl implements SoupClient {

	private final OAuthService oAuthService;

	private Token accessToken;

	private int localPort;

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

		this.oAuthService = new ServiceBuilder().provider(SoupApi.class)
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

			requestToken = oAuthService.getRequestToken();

			// Open the browser to ask for permission
			askForUserPermission(oAuthService, requestToken);
		} catch (Exception exception) {
			throw new RuntimeException("Could not start authentication",
					exception);
		}

		// This will block until the user has granted or denied permission
		try {
			accessToken = oAuthService.getAccessToken(requestToken, verifier);
		} catch (NotAuthorizedException notAuthorizedException) {
			// User clicked on deny
			throw notAuthorizedException;
		}

		return OAuthHelper.createAccessTokenProperties(accessToken);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.metalab.m68k.soup.SoupClient#getUser()
	 */
	public User getUser() throws NotAuthorizedException {
		return send(new BodylessJsonRequestImpl<User>(Verb.GET,
				soupApi("/authenticate")) {

			@Override
			protected User unmarshalJson(Response response)
					throws JsonProcessingException, IOException {
				return User.create(OM.readTree(response.getBody()).get("user"));
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.metalab.m68k.soup.SoupClient#post(at.metalab.m68k.soup.resource.Blog,
	 * at.metalab.m68k.soup.resource.posts.Postable)
	 */
	public PostResult post(Blog blog, Postable post)
			throws NotAuthorizedException {
		return send(post.createRequest(blog));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.metalab.m68k.soup.SoupClient#groupSearch(java.lang.String)
	 */
	public List<Group> groupSearch(final String query)
			throws NotAuthorizedException {
		return send(new BodylessJsonRequestImpl<List<Group>>(Verb.GET,
				soupApi(String.format("/groups/user/search?q=%s", query))) {

			@Override
			protected List<Group> unmarshalJson(Response response)
					throws JsonProcessingException, IOException {
				List<Group> groups = new ArrayList<Group>();

				JsonNode rootNode = OM.readTree(response.getBody());
				for (JsonNode groupNode : rootNode.findValue("groups")) {
					groups.add(Group.create(groupNode));
				}

				return groups;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.metalab.m68k.soup.SoupClient#groupJoin(at.metalab.m68k.soup.resource
	 * .Group)
	 */
	public Group groupJoin(Group group) throws NotAuthorizedException {
		return send(new BodylessJsonRequestImpl<Group>(Verb.PUT,
				soupApi(String.format("/groups/user/%d", group.getId()))) {

			@Override
			protected Group unmarshalJson(Response response)
					throws JsonProcessingException, IOException {
				return Group.create(OM.readTree(response.getBody()));
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * at.metalab.m68k.soup.SoupClient#groupLeave(at.metalab.m68k.soup.resource
	 * .Group)
	 */
	public Group groupLeave(Group group) throws NotAuthorizedException {
		return send(new BodylessJsonRequestImpl<Group>(Verb.DELETE,
				soupApi(String.format("/groups/user/%d", group.getId()))) {

			@Override
			protected Group unmarshalJson(Response response)
					throws JsonProcessingException, IOException {
				return Group.create(OM.readTree(response.getBody()));
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.metalab.m68k.soup.SoupClient#groupsJoined()
	 */
	public List<Group> groupsJoined() throws NotAuthorizedException {
		return send(new BodylessJsonRequestImpl<List<Group>>(Verb.GET,
				soupApi("/groups/user")) {

			@Override
			protected List<Group> unmarshalJson(Response response)
					throws JsonProcessingException, IOException {
				List<Group> groups = new ArrayList<Group>();

				JsonNode rootNode = OM.readTree(response.getBody());
				for (JsonNode groupNode : rootNode.findValue("groups")) {
					groups.add(Group.create(groupNode));
				}

				return groups;
			}
		});
	}

	public Group groupCreate(final Group group) throws NotAuthorizedException {
		return send(new JsonRequestImpl<Group>() {
			@Override
			protected at.metalab.m68k.soup.http.requests.JsonRequestImpl.JsonInput createJsonInput() {
				ObjectNode rootNode = OM.createObjectNode();

				ObjectNode groupNode = OM.createObjectNode();
				rootNode.put("group", groupNode);

				groupNode.put("name", group.getName());
				groupNode.put("title", group.getTitle());
				groupNode.put("privacy", group.getPrivacy().getPropertyValue());
				groupNode.put("image_url", group.getImageUrl());

				try {
					String body = OM.writerWithDefaultPrettyPrinter()
							.writeValueAsString(rootNode);

					return new JsonInput(Verb.POST, soupApi("/groups"), body);
				} catch (IOException ioException) {
					throw new RuntimeException("Couldn't marshal json",
							ioException);
				}
			}

			@Override
			protected Group unmarshalJson(Response response)
					throws JsonProcessingException, IOException {
				return Group.create(OM.readTree(response.getBody())
						.get("group"));
			}
		});
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
	 * @param soupRequest
	 * @return
	 */
	private <E> E send(SoupRequest<E> soupRequest) {
		return soupRequest.send(oAuthService, accessToken);
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

}
