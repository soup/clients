package at.metalab.m68k.soup.resource.posts;

import java.io.InputStream;

/**
 * https://github.com/soup/clients/tree/master/v1.1#files
 * 
 * @author m68k
 * 
 */
public class FileUpload {
	private String tags;

	private String url;

	private String description;

	private String filename;

	private InputStream data;

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

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
