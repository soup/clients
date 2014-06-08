package at.metalab.m68k.soup.http;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public interface SoupRequest<E> {
	E send(OAuthService oAuthService, Token accessToken);
}
