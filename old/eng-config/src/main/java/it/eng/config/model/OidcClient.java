package it.eng.config.model;

import java.util.ArrayList;
import java.util.List;

public class OidcClient {
	public static String client_id;
	public static String client_secret;
	public static List<String> redirect_uris = new ArrayList<String>();
}