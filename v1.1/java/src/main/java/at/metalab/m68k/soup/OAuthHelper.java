package at.metalab.m68k.soup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.scribe.model.Token;

import at.metalab.m68k.soup.resource.User;

public class OAuthHelper {
	public static Properties loadApiProperties(InputStream inputStream)
			throws IOException {
		Properties properties = new Properties();
		properties.load(inputStream);

		return properties;
	}

	/**
	 * load the OAuth API Key and secret from the soup_api.properties file in
	 * the home directory of the current user
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Properties loadApiProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(new File(System
				.getProperty("user.home"), "soup_api.properties")));

		return properties;
	}

	public static Properties loadAccessTokenProperties(InputStream inputStream) {
		Properties properties = new Properties();

		try {
			properties.load(inputStream);
		} catch (IOException ioException) {
			// ignore
		}

		return properties;
	}

	public static Properties loadAccessTokenProperties() {
		try {
			return loadAccessTokenProperties(new FileInputStream(new File(
					System.getProperty("user.home"),
					"soup_accesstoken.properties")));
		} catch (FileNotFoundException fileNotFoundException) {
			return new Properties();
		}
	}

	public final static String SOUP_ACCESSTOKEN_TOKEN = "soup.accesstoken.token";
	public final static String SOUP_ACCESSTOKEN_SECRET = "soup.accesstoken.secret";

	public static Properties createAccessTokenProperties(Token token) {
		Properties properties = new Properties();
		properties.put(SOUP_ACCESSTOKEN_TOKEN, token.getToken());
		properties.put(SOUP_ACCESSTOKEN_SECRET, token.getSecret());

		return properties;
	}

	public static void storeAccessTokenProperties(
			Properties accesstokenProperties, User user) {
		try {
			accesstokenProperties.store(
					new FileOutputStream(new File(System
							.getProperty("user.home"),
							"soup_accesstoken.properties")), String.format(
							"%s (%s)", user.getName(), user.getResource()));
		} catch (Exception exception) {
			System.out.println("Could not store access token: "
					+ exception.getMessage());
		}
	}
}
