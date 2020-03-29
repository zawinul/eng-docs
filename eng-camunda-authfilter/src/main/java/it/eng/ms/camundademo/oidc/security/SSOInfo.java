package it.eng.ms.camundademo.oidc.security;

import java.util.Date;

import org.camunda.bpm.engine.impl.util.json.JSONObject;



public class SSOInfo {
	public String userId;
	public String accessToken;
	public String idToken;
	public long expireIn;
	public String scope;
	public String issuer;
	public String audience;
	public Date exp;
	public String email;
	public boolean email_verified;
	public JSONObject userInfo;
	
	public String camunda_id;
}