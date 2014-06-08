package at.metalab.m68k.soup.http;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import at.metalab.m68k.soup.resource.PostResult;

public class PostRequest implements SoupRequest<PostResult> {

	public PostResult send(OAuthService oAuthService, Token accessToken) {
		return null;
	}
}
