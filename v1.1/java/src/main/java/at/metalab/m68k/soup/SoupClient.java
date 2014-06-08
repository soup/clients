package at.metalab.m68k.soup;

import java.util.List;
import java.util.Properties;

import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.Group;
import at.metalab.m68k.soup.resource.PostResult;
import at.metalab.m68k.soup.resource.User;
import at.metalab.m68k.soup.resource.posts.Postable;

/**
 * @author m68k
 *
 */
public interface SoupClient {
	/**
	 * @return
	 * @throws NotAuthorizedException
	 */
	Properties authenticate() throws NotAuthorizedException;

	/**
	 * @return
	 * @throws NotAuthorizedException
	 */
	User getUser() throws NotAuthorizedException;

	/**
	 * @param blog
	 * @param post
	 * @return
	 * @throws NotAuthorizedException
	 */
	PostResult post(Blog blog, Postable post) throws NotAuthorizedException;

	/**
	 * @param query
	 * @return
	 * @throws NotAuthorizedException
	 */
	List<Group> groupSearch(String query) throws NotAuthorizedException;

	/**
	 * @param group
	 * @return
	 * @throws NotAuthorizedException
	 */
	Group groupJoin(Group group) throws NotAuthorizedException;

	/**
	 * @param group
	 * @return
	 * @throws NotAuthorizedException
	 */
	Group groupLeave(Group group) throws NotAuthorizedException;

	/**
	 * @return
	 * @throws NotAuthorizedException
	 */
	List<Group> groupsJoined() throws NotAuthorizedException;
	
	/**
	 * @param group
	 * @return
	 * @throws NotAuthorizedException
	 */
	Group groupCreate(Group group) throws NotAuthorizedException;
}
