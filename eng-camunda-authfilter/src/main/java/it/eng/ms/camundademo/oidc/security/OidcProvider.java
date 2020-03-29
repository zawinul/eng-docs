package it.eng.ms.camundademo.oidc.security;

import org.camunda.bpm.engine.impl.util.json.JSONObject;

public abstract class OidcProvider {
	protected abstract String getName();
	protected abstract String getScopes();
	protected abstract String getAuthUrl();
	protected abstract String getTokenUrl();
	protected abstract String getUserInfoUrl();
	protected abstract String getClientId();
	protected abstract String getClientSecret();
	protected abstract String getCallbackUrl();
	protected abstract String getCallbackUri();
	protected abstract String getLoginUri();
	protected abstract String getCamundaId(JSONObject info);
	protected abstract String getIssuer();
}
