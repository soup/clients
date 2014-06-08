package at.metalab.m68k.soup.resource.posts;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ObjectNode;

import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

/**
 * https://github.com/soup/clients/tree/master/v1.1#links
 * 
 * @author m68k
 * 
 */
public class Link extends AbstractPost {

	@Override
	protected SoupRequestBuilder<PostResult> createPost(Blog blog) {
		return new JsonPostTemplate(blog, "/posts/links") {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("url", getUrl());
				postNode.put("description", getDescription());
				postNode.put("caption", getCaption());
				postNode.put("tags", getTags());
			}
		};
	}

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
