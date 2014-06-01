package at.metalab.m68k.soup.resource.posts;

/**
 * https://github.com/soup/clients/tree/master/v1.1#video
 * 
 * @author m68k
 * 
 */
public class Video {

	private String url;

	private String embedCode;

	private String description;

	private String tags;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public void setEmbedCode(String embedCode) {
		this.embedCode = embedCode;
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
