package at.metalab.m68k.soup.examples;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Properties;

import at.metalab.m68k.soup.NotAuthorizedException;
import at.metalab.m68k.soup.OAuthHelper;
import at.metalab.m68k.soup.SoupClient;
import at.metalab.m68k.soup.SoupClientImpl;
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

public class SoupPostExample {

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

		System.out.println(String.format("You have connected as %s",
				user.getName()));

		Blog blog = null;

		// select the blog which represents the users own soup
		for (Blog writeableBlog : user.getBlogs()) {
			if (writeableBlog.getPermissions().contains("owner")) {
				blog = writeableBlog;
				break;
			}
		}

		System.out.println(String.format("Posting to soup '%s'",
				blog.getTitle()));

		// uncomment any posts you want to try

		// postText(soup, blog);
		//
		// postLink(soup, blog);
		//
		// postImage(soup, blog);
		//
		// postImageUpload(soup, blog);
		//
		// postReview(soup, blog);
		//
		// postEvent(soup, blog);
		//
		// postQuote(soup, blog);
		//
		// postVideo(soup, blog);
	}

	private static void postVideo(SoupClient soup, Blog blog) {
		Video video = new Video();
		video.setDescription("the description");
		video.setUrl("http://www.youtube.com/watch?v=_J7jb2AX1sk");
		video.setTags("foo bar quux");
		soup.post(blog, video);
	}

	private static void postQuote(SoupClient soup, Blog blog) {
		Quote quote = new Quote();
		quote.setQuote("the quote");
		quote.setSource("the source");
		quote.setTags("foo bar quux");
		soup.post(blog, quote);
	}

	private static void postEvent(SoupClient soup, Blog blog) {
		Event event = new Event();
		event.setDescription("event description");
		event.setLocation("milliways");
		event.setStartDate(Calendar.getInstance());
		event.setEndDate(Calendar.getInstance());
		event.setTitle("suppentreffen");
		event.setTags("foo bar quux");
		soup.post(blog, event);
	}

	private static void postReview(SoupClient soup, Blog blog) {
		Review review = new Review();
		review.setRating(5);
		review.setReview("soup api is nice");
		review.setTags("foo bar quux");
		review.setTitle("review of the soup api");
		review.setUrl("https://github.com/soup/clients/tree/master/v1.1");
		soup.post(blog, review);
	}

	private static void postImage(SoupClient soup, Blog blog) {
		Image image = new Image();
		image.setDescription("foo");
		image.setUrl("http://2.bp.blogspot.com/_cd6_MFUGTUE/S1MYZg7Cg5I/AAAAAAAAAOM/wMZw6bMjE14/s320/amok_01.jpg");
		image.setSource("http://2.bp.blogspot.com/_cd6_MFUGTUE/S1MYZg7Cg5I/AAAAAAAAAOM/wMZw6bMjE14/s320/amok_01.jpg");
		image.setTags("foo bar quux");
		soup.post(blog, image);
	}

	private static void postImageUpload(SoupClient soup, Blog blog)
			throws FileNotFoundException {
		Image image = new Image();
		image.setDescription("Soup badge");
		image.setSource("http://www.soup.io");
		image.setTags("java soup api");
		image.setData(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("examples/soup_badge.png"));

		PostResult postResult = soup.post(blog, image);
		System.out.println(postResult);
	}

	private static void postLink(SoupClient soup, Blog blog) {
		Link link = new Link();
		link.setCaption("the caption");
		link.setDescription("the description");
		link.setUrl("http://foo-bar.corp.fu");
		link.setTags("foo bar quux");
		soup.post(blog, link);
	}

	private static void postText(SoupClient soup, Blog blog) {
		Regular regular = new Regular();
		regular.setBody("the body");
		regular.setTitle("the title");
		regular.setTags("foo bar quux");
		soup.post(blog, regular);
	}
}
