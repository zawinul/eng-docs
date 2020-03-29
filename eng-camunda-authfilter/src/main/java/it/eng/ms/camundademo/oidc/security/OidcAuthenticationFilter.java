package it.eng.ms.camundademo.oidc.security;

import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;
import it.eng.ms.camundademo.oidc.EngOidcTokenVerifier;
import it.eng.ms.camundademo.utils.UTF8;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.util.json.JSONException;
import org.camunda.bpm.engine.impl.util.json.JSONObject;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.camunda.bpm.webapp.impl.security.auth.UserAuthentication;
import org.jose4j.jwt.JwtClaims;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;


// libero adattamento di https://github.com/camunda-consulting/camunda-webapp-plugins/tree/master/camunda-webapp-plugin-sso-autologin


@Configuration
public class OidcAuthenticationFilter implements Filter {
	
	private static final boolean ONLY_EXISTING_USERS = true;
	private static final String[] APPS = new String[] { "cockpit", "tasklist", "welcome" };

	OidcProvider google = new GoogleOidcProvider();
	OidcProvider eng = new EngOidcProvider();
	
	OidcProvider providers[] = { google, eng };	
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		try {
			SSLUtil.turnOffSslChecking();
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				
				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse resp = (HttpServletResponse) response;
			String uri = req.getRequestURI();
			System.out.println("\t>>> "+req.getMethod()+" "+uri);
			
			if (uri.startsWith("/rest/")) {
				serveRest(request, response, chain);
				return;
			}
			
			Authentications sessionAuthentications = Authentications.getFromSession(req.getSession());
			boolean autenticato = sessionAuthentications!=null && sessionAuthentications.getAuthentications().size()>0;
			
			if (!autenticato) {
				// è una delle pagine di login?
				for(OidcProvider provider: providers) {
					if (uri.equals(provider.getLoginUri())) {
						String authUrl = urlToSSOAuth(provider, "some data", req);
						System.out.println("redirect to "+authUrl);
						((HttpServletResponse) response).sendRedirect(authUrl);
						return;
					}
				}
				
				// è una delle pagine di callback?
				for(OidcProvider provider: providers) {
					if (uri.equals(provider.getCallbackUri())) {
						processOidcCallback(request, response, chain);
						return;
					}
				}
			}
			
		} 
		catch (Exception e) {
			throw new ServletException(e);
		}
		
		// se non sono uscito prima continua con il normale processing di camunda
		chain.doFilter(request, response);
	}

	private void serveRest(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse resp = (HttpServletResponse) response;
	        resp.setCharacterEncoding("UTF-8");
	        
			// CORS management su rest (da parametrizzare)
	        if (resp.getHeader("Access-Control-Allow-Origin")==null)
	        	resp.addHeader("Access-Control-Allow-Origin", "*");
			//resp.addHeader("Access-Control-Allow-Origin", "localhost:9010");
			resp.addHeader("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
	        resp.addHeader("Access-Control-Allow-Method", "GET, PUT, POST, OPTIONS, X-XSRF-TOKEN");
	        resp.addHeader("Access-Control-Allow-Credentials", "true");
	        
	        if (req.getMethod().toUpperCase().equals("OPTIONS")) {
				chain.doFilter(request,  response);
				return;
			}

	        
			String auth = req.getHeader("Authorization");
			
			// caso eccezionale: permettiamo l'upload dal modeler senza autenticazione
			String url = req.getRequestURL().toString();
			if (url.equals("http://localhost:8079/rest/deployment/create")) {
				chain.doFilter(request,  response);
				return;
			};
			
			if (auth==null || !auth.startsWith("Bearer ")) {
				unauthorized("expected header = 'Authorization: Bearer <token>'", response);
				return;
			}
			String token = auth.substring(7).trim();
			EngOidcTokenVerifier v = new EngOidcTokenVerifier();
			JwtClaims c = v.valida(token);
			
			if (c==null) {
				unauthorized("invalid or expired token", response);
				return;				
			}
			JSONObject userInfo = getUserInfo(c);
			String id = null;
			for(OidcProvider provider: providers)
				if (c.getIssuer().equals(provider.getIssuer()))
					id = provider.getCamundaId(userInfo);

			if (id==null) {
				unauthorized("user has not a camunda_id", response);
				return;				
			}
			chain.doFilter(request,  response);
		} 
		catch (Exception e) {
			e.printStackTrace();
			unauthorized(e.getMessage(), response);
		} 
	}
	
	private void unauthorized(String message, ServletResponse response) {
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.setStatus(401);
		try {
			resp.getWriter().print(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void processOidcCallback(ServletRequest request, ServletResponse response, FilterChain chain) throws Exception {
		//SSLUtil.turnOffSslChecking();
		HttpServletRequest req = (HttpServletRequest) request;
		System.out.println(req.getQueryString());
		for(String key: req.getParameterMap().keySet())
			System.out.println("  par "+key+" = "+req.getParameter(key));
		String code = req.getParameter("code");
		String state = UTF8.fromBase64(req.getParameter("state"));
		JSONObject o = new JSONObject(state);
		String returnedProvider = o.getString("provider");
		OidcProvider provider = null;
		for(OidcProvider p: providers) {
			if (returnedProvider.equals(p.getName()))
				provider = p;
		}
		assert(provider!=null);
		SSOInfo info = getSSOInfo(provider, code);
		String id = provider.getCamundaId(info.userInfo);
		Authentications sessionAuthentications = Authentications.getFromSession(req.getSession());
		addAuthentication(id, sessionAuthentications);
		Authentications.updateSession(req.getSession(), sessionAuthentications);
		((HttpServletResponse) response).sendRedirect("/");
	}

//	private void OLD_processOidcCallback(ServletRequest request, ServletResponse response, FilterChain chain) throws Exception {
//		//SSLUtil.turnOffSslChecking();
//		HttpServletRequest req = (HttpServletRequest) request;
//		System.out.println(req.getQueryString());
//		String code = req.getParameter("code");
//		String state = UTF8.fromBase64(req.getParameter("state"));
//		JSONObject o = new JSONObject(state);
//		String returnedProvider = o.getString("provider");
//		OidcProvider provider = null;
//		for(OidcProvider p: providers) {
//			if (returnedProvider.equals(p.getName()))
//				provider = p;
//		}
//		assert(provider!=null);
//		SSOInfo info = getSSOInfo(provider, code);
//		String id = provider.getCamundaId(info.userInfo);
//		Authentications sessionAuthentications = Authentications.getFromSession(req.getSession());
//		addAuthentication(id, sessionAuthentications);
//		Authentications.updateSession(req.getSession(), sessionAuthentications);
//		((HttpServletResponse) response).sendRedirect("/");
//	}

	private void addAuthentication(String username, Authentications authentications) {
		ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();

		User user = engine.getIdentityService().createUserQuery().userId(username).singleResult();
		if (user==null && ONLY_EXISTING_USERS)
			return;
		
		// get tenants
		List<String> tenantIds = new ArrayList<String>();
		List<Tenant> tenants = engine.getIdentityService().createTenantQuery().userMember(username).includingGroupsOfUser(true).list();
		for (Tenant tenant : tenants) 
			tenantIds.add(tenant.getId());

		// get groups
		List<String> groupIds = new ArrayList<String>();
		List<Group> groups = engine.getIdentityService().createGroupQuery().groupMember(username).list();
		for (Group group : groups) 
			groupIds.add(group.getId());

		// check user's app authorizations by iterating of list of apps and ask if permitted
		AuthorizationService authorizationService = engine.getAuthorizationService();
		HashSet<String> authorizedApps = new HashSet<String>();
		authorizedApps.add("admin");
		if (engine.getProcessEngineConfiguration().isAuthorizationEnabled()) {
			for (String application : APPS) 
				if (authorizationService.isUserAuthorized(username, groupIds, ACCESS, APPLICATION, application)) 
					authorizedApps.add(application);
		} 
		else 
			Collections.addAll(authorizedApps, APPS);
		
		// create new authentication object to store authentication
		UserAuthentication newAuthentication = new UserAuthentication(username, engine.getName());
		newAuthentication.setGroupIds(groupIds);
		newAuthentication.setTenantIds(tenantIds);
		newAuthentication.setAuthorizedApps(authorizedApps);

		// and add the new logged in user
		authentications.addAuthentication(newAuthentication);		
	}

	public String urlToSSOAuth(OidcProvider provider, String callerParams, HttpServletRequest req)  {
		String url;
		try {
			String mark = Math.random()+"+"+(new Date()).getTime();
			String nonce = Math.random()+"+"+(new Date()).getTime();
			String encnonce = UTF8.toBase64(nonce);
			JSONObject jstate = new JSONObject();
			jstate 
				.put("nonce", nonce)
				.put("mark", mark)
				.put("client", provider.getClientId())
				.put("provider", provider.getName())
				.put("params", StringEscapeUtils.escapeHtml(callerParams));
			String state = jstate.toString();
			String encstate = UTF8.toBase64(state);
			
			url = provider.getAuthUrl() 
					+"?response_type=code"
					+"&state="+encstate
					+"&nonce="+encnonce
					+"&client_id="+provider.getClientId()
					+"&scope="+UTF8.urlEncode(provider.getScopes())
					+"&redirect_uri="+UTF8.urlEncode(provider.getCallbackUrl());
			return url;

		} catch (Exception e) {
			e.printStackTrace();
			return "Error: "+e.getMessage();
		}			
	}

	public SSOInfo getSSOInfo(OidcProvider provider, String code)   {
		try {
			SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
			rf.setOutputStreaming(false);
			ClientHttpRequest req = rf.createRequest(new URI(provider.getTokenUrl()), HttpMethod.POST);
			String auth = UTF8.toBase64(provider.getClientId()+":"+provider.getClientSecret());
			req.getHeaders().add("Content-Type", "application/x-www-form-urlencoded");
			req.getHeaders().add("Authorization", "Basic "+auth);
			String body="grant_type=authorization_code&response_type=code";
			body+="&code="+UTF8.urlEncode(code);
			body+="&scope="+UTF8.urlEncode(provider.getScopes());
			body+="&redirect_uri="+UTF8.urlEncode(provider.getCallbackUrl());
			req.getBody().write(UTF8.bytes(body));
			ClientHttpResponse resp = req.execute();

			InputStream is = resp.getBody();
			String rbody = UTF8.streamToString(is); 
			SSOInfo ssoInfo = parseGetTokenResponse(rbody);
			ssoInfo.userInfo = getUserInfo(provider, ssoInfo.accessToken);
			return ssoInfo;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	private SSOInfo parseGetTokenResponse(String response) {
		try {
			SSOInfo info = new SSOInfo();
			JSONObject x = new JSONObject(response);
			info.expireIn = x.getLong("expires_in");
			info.idToken = x.getString("id_token");
			info.accessToken = x.getString("access_token");
			String j = UTF8.fromBase64(info.idToken.split("[.]")[1]);
			x = new JSONObject(j);
			info.email = x.optString("email");
			info.email_verified = x.optBoolean("email_verified");

			info.issuer = x.optString("iss");
			info.audience = x.optString("aud");
			info.exp = new Date(x.optLong("exp")*1000L);
			info.userId = x.optString("sub");
			return info;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void fillUser_Info(OidcProvider provider, SSOInfo info)  {
		String url = provider.getUserInfoUrl();
		if (url==null)
			return;
		try {
			SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
			rf.setOutputStreaming(false);
			ClientHttpRequest req = rf.createRequest(new URI(url), HttpMethod.GET);
			req.getHeaders().add("Authorization", "Bearer "+info.accessToken);
			ClientHttpResponse resp = req.execute();

			InputStream is = resp.getBody();
			String rbody = UTF8.streamToString(is);
			info.userInfo = new JSONObject(rbody);
			System.out.println(info.userInfo.toString(2));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JSONObject getUserInfo(JwtClaims claims) {
		try {
			return new JSONObject(claims.getRawJson());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	private JSONObject getUserInfo(OidcProvider provider, String access_token)  {
		System.out.println("token len:" +access_token.length()+" hash:"+access_token.hashCode());
		System.out.println("\n" +access_token+"\n");

		String url = provider.getUserInfoUrl();
		if (url==null)
			return null;
		try {
			SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
			rf.setOutputStreaming(false);
			ClientHttpRequest req = rf.createRequest(new URI(url), HttpMethod.GET);
			req.getHeaders().add("Authorization", "Bearer "+access_token);
			ClientHttpResponse resp = req.execute();

			InputStream is = resp.getBody();
			String rbody = UTF8.streamToString(is);
			return new JSONObject(rbody);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}