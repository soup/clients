package at.metalab.m68k.soup.resource.posts;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.node.ObjectNode;

import at.metalab.m68k.soup.http.SoupRequestBuilder;
import at.metalab.m68k.soup.resource.Blog;
import at.metalab.m68k.soup.resource.PostResult;

/**
 * https://github.com/soup/clients/tree/master/v1.1#reviews
 * 
 * @author m68k
 */
public class Review extends AbstractPost {

	@Override
	protected SoupRequestBuilder<PostResult> createPost(Blog blog) {
		return new JsonPostTemplate(blog, "/posts/reviews") {

			@Override
			protected void buildPostNode(ObjectNode postNode)
					throws IOException, JsonMappingException,
					JsonParseException {
				postNode.put("url", getUrl());
				postNode.put("rating", getRating());
				postNode.put("review", getReview());
				postNode.put("title", getTitle());
				postNode.put("tags", getTags());
			}
		};
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTags() {
		return tags;
	}

}
