package at.metalab.m68k.soup.examples;

import java.util.Arrays;
import java.util.Properties;

import at.metalab.m68k.soup.NotAuthorizedException;
import at.metalab.m68k.soup.OAuthHelper;
import at.metalab.m68k.soup.SoupClient;
import at.metalab.m68k.soup.SoupClientImpl;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.User;

public class SoupLoginExample {

	public static void main(String[] args) throws Exception {
		Properties soupApiProperties = OAuthHelper.loadApiProperties();
		Properties accessTokenProperties = OAuthHelper
				.loadAccessTokenProperties();

		SoupClient soup = new SoupClientImpl(soupApiProperties,
				accessTokenProperties, 1337);

		User user = null;
		try {
			user = soup.getUser();
		} catch (NotAuthorizedException notAuthorizedException) {
			accessTokenProperties = soup.authenticate();
			user = soup.getUser();

			OAuthHelper.storeAccessTokenProperties(accessTokenProperties, user);
		}

		System.out.println(String.format("user.name=%s", user.getName()));
		System.out
				.println(String.format("user.resource=%s", user.getResource()));
		System.out.println(String.format(
				"found %d blogs to which the user has write access:", user
						.getBlogs().size()));
		System.out.println();

		for (Blog blog : user.getBlogs()) {
			System.out.println(String.format("blog.name=%s", blog.getName()));
			System.out.println(String.format("blog.resource=%s",
					blog.getResource()));
			System.out.println(String.format("blog.title=%s", blog.getTitle()));
			System.out.println(String.format("blog.url=%s", blog.getUrl()));
			System.out.println(String.format("blog.permissions=%s",
					Arrays.toString(blog.getPermissions().toArray())));
			System.out.println();
		}
	}
}
