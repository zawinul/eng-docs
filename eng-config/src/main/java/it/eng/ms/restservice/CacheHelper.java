package it.eng.ms.restservice;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import it.eng.ms.restservice.security.EngOidcTokenVerifier;
import it.eng.ms.restservice.security.GetUserInfo;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.jose4j.jwt.JwtClaims;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheHelper {
	
	private static Logger logger = LoggerFactory.getLogger("cache");

	static {
		init();
	}
	
	public static void init() {
		_init();
	}

	public static void terminate() {
		_terminate();
	}

	public static void reset() {
		verifiedTokens2.clear();
		unverifiedTokens2.clear();
		userInfoFromToken.clear();
	}

	// PRIVATE ---
	//private static JwtClaims nullJwtClaims = new JwtClaims();
	public static JwtClaims tokenToClaims(String token) {
		
		logger.debug("token to claims "+token);
		try {
			if (verifiedTokens2.containsKey(token)) {
				logger.debug("già verificato");
				return verifiedTokens2.get(token);
			}
			if (unverifiedTokens2.containsKey(token)) {
				logger.debug("non verificato, da cache");
				return null;
			}
			
			
			JwtClaims claims = verifier.valida(token);
			long exp = claims==null ? Long.MIN_VALUE: claims.getExpirationTime().getValueInMillis();
			
			boolean ok = (claims!=null) && exp>System.currentTimeMillis();
			if (!ok) {
				logger.debug("non verificato");
				unverifiedTokens2.put(token,  Boolean.valueOf(false));
				long t = System.currentTimeMillis()+5L*60L*1000L;
				logger.debug("unverified token expire = "+new Date(t));
				unverifiedTokens2.expireAt(token, t);
				return null;
			}
			
			logger.debug("verificato");
			unverifiedTokens2.remove(token);
			verifiedTokens2.put(token, claims);
			verifiedTokens2.expireAt(token, claims.getExpirationTime().getValueInMillis());
			return claims;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static JSONObject tokenToUserInfo(String token) {
		
		try {
			logger.debug("token to user info "+token);
			JwtClaims claims = tokenToClaims(token);
			if (claims==null) {
				logger.debug("token non associabile a claims");
				return null;
			}
			JSONObject info = userInfoFromToken.get(token);
			if (info!=null) {
				logger.debug("info trovate in cache");
				return info;
			}
			logger.debug("get info from oidc provider");
			String json = GetUserInfo.get(token);
			if (json==null) {
				logger.debug("info not found");
				return null;
			}
			JSONObject obj = new JSONObject(json);
			logger.debug("found info");
			userInfoFromToken.put(token,  obj);
			return obj;
		} catch (JSONException e) {
			logger.debug("", e);
			return null;
		}
	}
	
	private static void _init() {

		logger.info("Init Cache Helper");
		try {
			verifier = new EngOidcTokenVerifier();
		} catch (Exception e) {
			logger.debug("cannot initialize EngOidcTokenVerifier",e);
		}

		verifiedTokens2 = new Cache2kBuilder<String, JwtClaims>() {}
			.name("verifiedToken")
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.build();

		unverifiedTokens2 = new Cache2kBuilder<String, Boolean>() {}
			.name("unverifiedTokens")
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.build();
			
		userInfoFromToken = new Cache2kBuilder<String, JSONObject>() {}
			.name("userInfoFromToken")
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.build();


		preloadThread.start();
	}

	private static Thread preloadThread = new Thread() {
		@Override
		public void run() {
			preload();
		}
	};

	
	private static void preload() {
		// ...
	}

	private static void _terminate() {
		verifiedTokens2.clearAndClose();
		unverifiedTokens2.clearAndClose();
		userInfoFromToken.clearAndClose();
	}

	private static Cache<String, JwtClaims> verifiedTokens2;
	private static Cache<String, Boolean> unverifiedTokens2;
	private static Cache<String, JSONObject> userInfoFromToken;
	private static EngOidcTokenVerifier verifier;

}
