package at.metalab.m68k.soup;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.scribe.oauth.OAuthService;

/**
 * Example for acessing the Soup API in Java. Authentication via OAuth and
 * displaying the user details from /authenticate. Based upon the
 * {@link Skyrock
 * https://github.com/fernandezpablo85/scribe-java/blob/master/src
 * /test/java/org/scribe/examples/SkyrockExample.java} example.
 * 
 * @author m68k
 * 
 */
public class SoupExample {

	/**
	 * Endpoints from {@link Github
	 * https://github.com/soup/clients/tree/master/v1.1#oauth-endpoints}.
	 * 
	 * @author m68k
	 * 
	 */
	public static class SoupApi implements Api {

		public OAuthService createService(OAuthConfig arg0) {
			return new OAuth10aServiceImpl(new DefaultApi10a() {

				@Override
				public String getRequestTokenEndpoint() {
					return "https://api.soup.io/oauth/request_token";
				}

				@Override
				public String getAuthorizationUrl(Token arg0) {
					return "https://api.soup.io/oauth/authorize";
				}

				@Override
				public String getAccessTokenEndpoint() {
					return "https://api.soup.io/oauth/access_token";
				}
			}, arg0);
		}
	}

	/**
	 * Verifier implementation which opens a server socket to listen for the
	 * OAuth callback request. This is not really production code ;)
	 * 
	 * @author m68k
	 * 
	 */
	private static class LocalCallbackVerifier extends Verifier {

		private final ServerSocket serverSocket;

		public LocalCallbackVerifier(int port) throws IOException {
			super("n/a"); // satisfy the java compiler and the scribe library

			serverSocket = new ServerSocket(port);
		}

		private final static String OAUTH_VERIFIER_PARAM = "oauth_verifier=";

		private final static String OAUTH_DENIED = "error=access_denied";

		@Override
		public String getValue() {
			try {
				Socket socket = serverSocket.accept();

				BufferedReader r = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				String line = null;

				while ((line = r.readLine()) != null) {
					if (line.contains(OAUTH_DENIED)) {
						throw new RuntimeException("User denied access");
					}

					if (line.contains(OAUTH_VERIFIER_PARAM)) {
						// remove everything before the param
						String verifier = line.substring(line
								.indexOf(OAUTH_VERIFIER_PARAM));

						// remove the param itself
						verifier = verifier.replace(OAUTH_VERIFIER_PARAM, "");

						// remove everything after the first blank (including
						// the blank) if found
						if (verifier.indexOf(' ') != -1) {
							verifier = verifier.substring(0,
									verifier.indexOf(' '));
						}

						// remove everything after (and including) the first
						// question mark if found
						if (verifier.indexOf("?") != -1) {
							verifier = verifier.substring(0,
									verifier.indexOf('?'));
						}

						// return the remaining stuff as the verifier
						return verifier;
					}
				}
			} catch (IOException ioException) {
				throw new RuntimeException(
						"Error while reading callback request from browser",
						ioException);
			}

			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		int localPort = 1337;

		// load the OAuth API Key and secret from the soup.properties file in
		// the home directory of the current user.
		Properties soupProperties = new Properties();
		soupProperties.load(new FileInputStream(new File(System
				.getProperty("user.home"), "soup.properties")));

		String yourApiKey = soupProperties.getProperty("api.key");
		String yourApiSecret = soupProperties.getProperty("api.secret");

		OAuthService service = new ServiceBuilder().provider(SoupApi.class)
				.apiKey(yourApiKey).apiSecret(yourApiSecret)
				.callback(String.format("http://127.0.0.1:%d", localPort))
				.build();

		System.out.println("=== Soup.io OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		Token requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println(requestToken.getRawResponse());

		// Instantiate the verifier first to have the server socket already
		// running
		Verifier verifier = new LocalCallbackVerifier(localPort);

		// Open the browser to ask for permission
		askForUserPermission(service, requestToken);

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");

		// This will block until the user has granted or denied permission
		Token accessToken = service.getAccessToken(requestToken, verifier);

		System.out.println("Got the Access Token!");
		System.out.println("(if you are curious it looks like this: "
				+ accessToken + " )");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		OAuthRequest request = new OAuthRequest(Verb.GET,
				soupApi("/authenticate"));
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println(String.format("HTTP-Status Code: %d",
				response.getCode()));

		System.out.println("Content:");
		String body = response.getBody();
		prettyPrintJson(body);
	}

	/**
	 * Pretty print a String containing JSON to System.out.
	 * 
	 * @param jsonString
	 * @throws Exception
	 */
	private static void prettyPrintJson(String jsonString) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		Object json = mapper.readValue(jsonString, Object.class);
		System.out.println(mapper.writerWithDefaultPrettyPrinter()
				.writeValueAsString(json));
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

}