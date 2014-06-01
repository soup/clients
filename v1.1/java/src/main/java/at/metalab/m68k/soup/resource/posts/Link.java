package at.metalab.m68k.soup.resource.posts;

/**
 * https://github.com/soup/clients/tree/master/v1.1#links
 * 
 * @author m68k
 * 
 */
public class Link {

	private String url;

	private String caption;

	private String description;

	private String tags;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

}
