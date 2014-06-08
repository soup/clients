package at.metalab.m68k.soup.resource.posts;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ObjectNode;

import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

/**
 * https://github.com/soup/clients/tree/master/v1.1#quotes
 * 
 * @author m68k
 * 
 */
public class Quote extends AbstractPost {

	@Override
	protected SoupRequestBuilder<PostResult> createPost(Blog blog) {
		return new JsonPostTemplate(blog, "/posts/quotes") {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("quote", getQuote());
				postNode.put("source", getSource());
				postNode.put("tags", getTags());
			}
		};
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
