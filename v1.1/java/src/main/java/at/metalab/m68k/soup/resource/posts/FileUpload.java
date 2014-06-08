package at.metalab.m68k.soup.resource.posts;

import java.io.InputStream;

import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

/**
 * https://github.com/soup/clients/tree/master/v1.1#files
 * 
 * @author m68k
 * 
 */
public class FileUpload extends AbstractPost {

	@Override
	protected SoupRequestBuilder<PostResult> createPost(Blog blog) {
		return new MultipartPostTemplate(blog, "/posts/files") {

			@Override
			protected String getTags() {
				return FileUpload.this.getTags();
			}

			@Override
			protected String getFilename() {
				return FileUpload.this.getFilename();
			}

			@Override
			protected String getDescription() {
				return FileUpload.this.getDescription();
			}

			@Override
			protected InputStream getData() {
				return FileUpload.this.getData();
			}
		};
	}

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
