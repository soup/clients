package at.metalab.m68k.soup;

import java.util.Properties;

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

public interface SoupClient {
	Properties authenticate() throws NotAuthorizedException;

	User getUser() throws NotAuthorizedException;

	PostResult post(Blog blog, Regular post) throws NotAuthorizedException;

	PostResult post(Blog blog, Link post) throws NotAuthorizedException;

	PostResult post(Blog blog, Image post) throws NotAuthorizedException;

	PostResult post(Blog blog, Review post) throws NotAuthorizedException;

	PostResult post(Blog blog, Event post) throws NotAuthorizedException;

	PostResult post(Blog blog, Quote post) throws NotAuthorizedException;

	PostResult post(Blog blog, Video post) throws NotAuthorizedException;
}
