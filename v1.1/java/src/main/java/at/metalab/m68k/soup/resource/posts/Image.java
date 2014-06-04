package at.metalab.m68k.soup.resource.posts;

import java.io.InputStream;

/**
 * https://github.com/soup/clients/tree/master/v1.1#images
 * 
 * @author m68k
 * 
 */
public class Image {

	private String url;

	private String source;

	private String description;

	private String tags;

	private InputStream data;

	public InputStream getData() {
		return data;
	}

	public void setData(InputStream data) {
		this.data = data;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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
