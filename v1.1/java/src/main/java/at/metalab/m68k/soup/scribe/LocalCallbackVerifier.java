package at.metalab.m68k.soup.scribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.scribe.model.Verifier;

import at.metalab.m68k.soup.NotAuthorizedException;

/**
 * @author m68k
 * 
 */
public class LocalCallbackVerifier extends Verifier {

	private final ServerSocket serverSocket;

	/**
	 * @param port
	 * @throws IOException
	 */
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
					try {
						socket.getOutputStream().write(":(".getBytes());
						socket.getOutputStream().flush();
					} catch (Exception ignore) {
					}

					throw new NotAuthorizedException();
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
						verifier = verifier.substring(0, verifier.indexOf(' '));
					}

					// remove everything after (and including) the first
					// question mark if found
					if (verifier.indexOf("?") != -1) {
						verifier = verifier.substring(0, verifier.indexOf('?'));
					}

					try {
						socket.getOutputStream().write(
								":)".getBytes("UTF-8"));
						socket.getOutputStream().flush();
					} catch (Exception ignore) {
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