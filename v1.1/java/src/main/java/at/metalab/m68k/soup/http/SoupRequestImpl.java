package at.metalab.m68k.soup.http;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public abstract class SoupRequestImpl<E> implements SoupRequest<E> {

	protected abstract OAuthRequest getRequest();

	protected abstract E convertResponse(Response response);

	private SoupErrorHandler soupErrorHandler = new SoupErrorHandler();

	public SoupRequestImpl() {
	}

	public SoupRequestImpl(SoupErrorHandler soupErrorHandler) {
		this.soupErrorHandler = soupErrorHandler;
	}

	public E send(OAuthService oAuthService, Token accessToken) {
		OAuthRequest request = getRequest();
		oAuthService.signRequest(accessToken, request);

		Response response = null;

		long tsStart = System.currentTimeMillis();
		try {
			System.out.println("[REQUEST] " + request.getVerb() + " "
					+ request.getCompleteUrl());
			response = request.send();
			System.out.println("> " + response.getBody());
		} finally {
			long tsEnd = System.currentTimeMillis();
			System.out.println("[REQUEST] SC=" + response.getCode() + " in "
					+ (tsEnd - tsStart) + "ms <- " + request.getVerb() + " "
					+ request.getCompleteUrl());
		}

		soupErrorHandler.handle(request, response);

		return convertResponse(response);
	}
}
