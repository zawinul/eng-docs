package it.eng.ms.camundademo.oidc.security;

import org.camunda.bpm.engine.impl.util.json.JSONArray;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.springframework.context.annotation.Configuration;



@Configuration
public class EngOidcProvider extends OidcProvider {
	
	 @Override
	 protected String getName() {
		 return "eng";
	 }

	@Override
	protected String getCamundaId(JSONObject info)  {
		try {
			JSONArray abilitazioni = info.optJSONArray("abilitazioni");
			if (abilitazioni==null)
				return null;
			for(int i=0; i<abilitazioni.length(); i++) {
				JSONObject ab = abilitazioni.getJSONObject(i);
				if (ab.getString("sistema").equals("workflow"))
					return ab.getString("camunda_id");
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// opein id connection flow #1 , see https://medium.com/@darutk/diagrams-of-all-the-openid-connect-flows-6968e3990660
	@Override
	protected String getScopes() {
		return "openid email profile dati_applicativi altro servizio1 workflow";

	}

	@Override
	protected String getAuthUrl() {
		return "https://oidc-provider:3043/auth";
	}

	@Override
	protected String getTokenUrl() {
		return "https://oidc-provider:3043/token";
	}

	@Override
	protected String getUserInfoUrl() {
		return "https://oidc-provider:3043/me";
	}

	@Override
	protected String getClientId() {
		return "camunda";
	}

	@Override
	protected String getClientSecret() {
		return "camundasecret";
	}
	

	@Override
	protected String getCallbackUrl() {
		return "https://127.0.0.1:8081/oidc-callback";
	}
	
	@Override
	protected String getCallbackUri() {
		return "/eng-oidc-callback";
	}

	@Override
	protected String getLoginUri() {
		return "/eng-sso";
	}
	
	@Override
	protected String getIssuer() {
		
		return "https://oidc-provider:3043/";
	}
	
}