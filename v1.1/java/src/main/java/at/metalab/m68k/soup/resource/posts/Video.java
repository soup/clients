package at.metalab.m68k.soup.resource.posts;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ObjectNode;

import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

/**
 * https://github.com/soup/clients/tree/master/v1.1#video
 * 
 * @author m68k
 * 
 */
public class Video extends AbstractPost {

	private final static String ENDPOINT = "/posts/videos";

	@Override
	protected SoupRequestBuilder<PostResult> createPost(Blog blog) {
		if (getData() != null) {
			return new MultipartPostTemplate(blog, ENDPOINT) {

				@Override
				protected String getTags() {
					return getTags();
				}

				@Override
				protected String getFilename() {
					return getFilename();
				}

				@Override
				protected String getDescription() {
					return getDescription();
				}

				@Override
				protected InputStream getData() {
					return getData();
				}
			};
		} else {
			return new JsonPostTemplate(blog, ENDPOINT) {

				@Override
				protected void buildPostNode(ObjectNode postNode)
						throws IOException, JsonMappingException,
						JsonParseException {
					postNode.put("url", getUrl());
					postNode.put("embed-code", getEmbedCode());
					postNode.put("description", getDescription());
					postNode.put("tags", getTags());
				}
			};
		}
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
