package at.metalab.m68k.soup.resource;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

/**
 * https://github.com/soup/clients/tree/master/v1.1#user-details
 * 
 * @author m68k
 * 
 */
public class User extends Resource {

	private String name;

	private List<Blog> blogs = new ArrayList<Blog>();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<Blog> getBlogs() {
		return blogs;
	}

	public static User create(JsonNode userNode) {
		User user = new User();
		user.setResource(userNode.get("resource").getTextValue());
		user.setName(userNode.get("name").getTextValue());

		for (JsonNode blogNode : userNode.findValue("blogs")) {
			user.getBlogs().add(Blog.create(blogNode.get("blog")));
		}

		return user;
	}
}
