package it.eng.config.model;

import java.util.ArrayList;
import java.util.List;

public class OidcProviderConfig {
	public String storage="mongo";
	public List<OidcClient> clients = new ArrayList<OidcClient>();
	public List<OidcClaims> claims = new ArrayList<OidcClaims>();
	public String url = "https://oidc-provider:3043";
	

	private static class OidcClaims {
		public String name;
		public List<String> values = new ArrayList<String>();
	}
}