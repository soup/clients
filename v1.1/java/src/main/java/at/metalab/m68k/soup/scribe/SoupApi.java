package at.metalab.m68k.soup.scribe;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.scribe.oauth.OAuthService;

/**
 * Endpoints from
 * https://github.com/soup/clients/tree/master/v1.1#oauth-endpoints
 * 
 * @author m68k
 * 
 */
public class SoupApi implements Api {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.scribe.builder.api.Api#createService(org.scribe.model.OAuthConfig)
	 */
	public OAuthService createService(OAuthConfig arg0) {
		return new OAuth10aServiceImpl(new DefaultApi10a() {

			@Override
			public String getRequestTokenEndpoint() {
				return "https://api.soup.io/oauth/request_token";
			}

			@Override
			public String getAuthorizationUrl(Token arg0) {
				return "https://api.soup.io/oauth/authorize";
			}

			@Override
			public String getAccessTokenEndpoint() {
				return "https://api.soup.io/oauth/access_token";
			}
		}, arg0);
	}
}