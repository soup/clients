package at.metalab.m68k.soup.resource.posts;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ObjectNode;

import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

/**
 * https://github.com/soup/clients/tree/master/v1.1#regular-text-post
 * 
 * @author m68k
 * 
 */
public class Regular extends AbstractPost {

	@Override
	protected SoupRequestBuilder<PostResult> createPost(Blog blog) {
		return new JsonPostTemplate(blog, "/posts/regular") {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("title", getTitle());
				postNode.put("body", getBody());
				postNode.put("tags", getTags());
			}
		};
	}

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
