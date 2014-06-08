package at.metalab.m68k.soup.http.requests;

import java.io.IOException;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import at.metalab.m68k.soup.http.SoupRequestImpl;

/**
 * @author m68k
 * 
 */
public abstract class JsonRequestImpl<E> extends SoupRequestImpl<E> {

	protected final static ObjectMapper OM = new ObjectMapper();

	protected static class JsonInput {
		private Verb verb;
		private String url;
		private String body;

		public JsonInput(Verb verb, String url, String body) {
			this.verb = verb;
			this.url = url;
			this.body = body;
		}

		public JsonInput(Verb verb, String url) {
			this(verb, url, null);
		}
	}

	protected abstract E unmarshalJson(Response response)
			throws JsonProcessingException, IOException;

	@Override
	protected E convertResponse(Response response) {
		try {
			return unmarshalJson(response);
		} catch (JsonProcessingException jsonProcessingException) {
			throw new RuntimeException("Unmarshalling json failed",
					jsonProcessingException);
		} catch (IOException ioException) {
			throw new RuntimeException("Unmarshalling json failed", ioException);
		}
	}

	private OAuthRequest createRequest(JsonInput jsonInput) {
		OAuthRequest post = new OAuthRequest(jsonInput.verb, jsonInput.url);
		post.addHeader("Content-Type", "application/json");
		if (jsonInput.body != null) {
			post.addPayload(jsonInput.body);
		}

		return post;
	}

	protected abstract JsonInput createJsonInput();

	@Override
	protected OAuthRequest getRequest() {
		return createRequest(createJsonInput());
	}
}