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
public class Blog extends Resource {

	private String title;

	private String name;

	private String imageUrl;

	private String url;

	private List<String> permissions = new ArrayList<String>();

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public static Blog create(JsonNode blogNode) {
		Blog blog = new Blog();
		blog.setResource(blogNode.get("resource").getTextValue());
		blog.setName(blogNode.get("name").getTextValue());
		blog.setTitle(blogNode.get("title").getTextValue());
		blog.setUrl(blogNode.get("url").getTextValue());
		blog.setImageUrl(blogNode.get("image_url").getTextValue());

		for (JsonNode permissionNode : blogNode.findValue("permissions")) {
			blog.getPermissions().add(permissionNode.asText());
		}

		return blog;
	}

}
