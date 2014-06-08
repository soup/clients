package at.metalab.m68k.soup.http.requests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.codehaus.jackson.JsonProcessingException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import at.metalab.m68k.soup.http.SoupRequestImpl;

public abstract class MultipartRequestImpl<E> extends SoupRequestImpl<E> {

	private final Verb verb;

	private String url;

	public MultipartRequestImpl(Verb verb, String url) {
		this.verb = verb;
		this.url = url;
	}

	public MultipartRequestImpl(Verb verb) {
		this(verb, null);
	}

	@Override
	protected OAuthRequest getRequest() {
		try {
			HttpEntity reqEntity = buildEntity();

			ByteArrayOutputStream o = new ByteArrayOutputStream();
			reqEntity.writeTo(o);

			OAuthRequest request = new OAuthRequest(verb, url);

			request.addHeader(reqEntity.getContentType().getName(), reqEntity
					.getContentType().getValue());
			request.addPayload(o.toByteArray());

			return request;
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected abstract HttpEntity buildEntity();

	protected abstract E unmarshalJson(Response response)
			throws JsonProcessingException, IOException;

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

	public void setUrl(String url) {
		this.url = url;
	}
}
