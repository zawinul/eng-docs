package it.eng.ms.camundademo.oidc.security;

import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.springframework.context.annotation.Configuration;

 

@Configuration
public class GoogleOidcProvider extends OidcProvider {
	 static final private String clientId="486323182812-7g2rm5msd8vs1bj9ebg9nncod4ms3m4i.apps.googleusercontent.com";
	 static final private String clientSecret="-TZsSb5vahJvUziw5JT-89Co";
	 static final private String accessTokenUri="https://www.googleapis.com/oauth2/v3/token";
	 static final private String userAuthorizationUri="https://accounts.google.com/o/oauth2/auth";
//	 static final private String issuer="accounts.google.com";
//	 static final private String jwkUrl="https://www.googleapis.com/oauth2/v2/certs";

	
	 @Override
	 protected String getName() {
		 return "google";
	 }

	@Override
	protected String getCamundaId(JSONObject info) {
//		if (info.email.equals("paolo.andrenacci@gmail.com"))
//			return "paolo";
//		else
			return "demo";
	}

	@Override
	protected String getScopes() {
		return "openid email profile";
	}

	@Override
	protected String getAuthUrl() {
		return userAuthorizationUri;
	}

	@Override
	protected String getTokenUrl() {
		return accessTokenUri;
	}

	@Override
	protected String getUserInfoUrl() {
		return null;
	}

	@Override
	protected String getClientId() {
		return clientId;
	}

	@Override
	protected String getClientSecret() {
		return clientSecret;
	}

	
	@Override
	protected String getCallbackUrl() {
		return "https://127.0.0.1:8081/oidc-callback";
	}
	
	@Override
	protected String getCallbackUri() {
		return "/oidc-callback";
	}
	

	@Override
	protected String getLoginUri() {
		return "/google-sso";
	}

	
	
	@Override
	protected String getIssuer() {
		return "???";
	}

}