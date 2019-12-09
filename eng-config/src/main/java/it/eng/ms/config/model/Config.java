package it.eng.ms.config.model;

import java.util.ArrayList;
import java.util.List;

public class Config {
	public OidcProviderConfig oidcProviderConfig;
	public OidcProviderInfo oidcProviderInfo;
	public List<String> oidcProviderEndpoint;
	
	public Servizio1Config servizio1Config;
	public Servizio1Info servizio1Info;
	public List<String> servizio1Endpoint;
	
	public App1Config app1Config;
	
	public static class OidcProviderConfig {
		public String storage="mongo";
		public List<OidcClient> clients = new ArrayList<OidcClient>();
		public List<OidcClaims> claims = new ArrayList<OidcClaims>();
		public String url = "https://oidc-provider:3043";
	}
	
	public static class OidcProviderInfo {
	}
	
	public static class Servizio1Config {
	}
	
	public static class Servizio1Info {
	}
	
	public static class App1Config {
	}
	
	public static class OidcClient {
		public static String client_id;
		public static String client_secret;
		public static List<String> redirect_uris = new ArrayList<String>();
	}
	
	public static class OidcClaims {
		public String name;
		public List<String> values = new ArrayList<String>();
	}

}
