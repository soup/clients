package at.metalab.m68k.soup.resource.posts;

/**
 * https://github.com/soup/clients/tree/master/v1.1#regular-text-post
 * 
 * @author m68k
 * 
 */
public class Regular {

	private String body;

	private String title;

	private String tags;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
