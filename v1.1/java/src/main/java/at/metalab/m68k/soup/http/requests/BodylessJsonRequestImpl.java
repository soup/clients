package at.metalab.m68k.soup.http.requests;

import org.scribe.model.Verb;

public abstract class BodylessJsonRequestImpl<E> extends
		JsonRequestImpl<E> {

	private final JsonInput jsonInput;

	public BodylessJsonRequestImpl(Verb verb, String url) {
		jsonInput = new JsonInput(verb, url);
	}

	@Override
	protected at.metalab.m68k.soup.http.requests.JsonRequestImpl.JsonInput createJsonInput() {
		return jsonInput;
	}
}
