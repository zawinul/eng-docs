package it.eng.ms.camundademo.oidc.security;

import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.webapp.impl.security.SecurityActions;
import org.camunda.bpm.webapp.impl.security.SecurityActions.SecurityAction;
import org.camunda.bpm.webapp.impl.security.auth.Authentication;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.camunda.bpm.webapp.impl.security.auth.UserAuthentication;
import org.springframework.context.annotation.Configuration;

// libero adattamento di https://github.com/camunda-consulting/camunda-webapp-plugins/tree/master/camunda-webapp-plugin-sso-autologin

/**
 * http://localhost:8080/camunda/?auto-login-username=admin
 * Of course this IS A SECURITY ISSUE! It is only meant to be used in test environments
 */
@Configuration
public class AutoLoginAuthenticationFilter /* implements Filter*/ {
	
	private static final boolean ONLY_EXISTING_USERS = true;
	private static final String[] APPS = new String[] { "cockpit", "tasklist" };

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final String username = req.getParameter("auto-login-username");
		
		Authentications sessionAuthentications = Authentications.getFromSession(req.getSession());
		
		// normal flow, nothing to do
		if (username==null && sessionAuthentications==null) {
			chain.doFilter(request, response);
			return;
		}
		
		ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();
		Authentication authentication = sessionAuthentications.getAuthenticationForProcessEngine(engine.getName());
		if (username != null && (authentication==null || !authentication.getName().equals(username)))
			addAuthentication(username, sessionAuthentications);

		Authentications.setCurrent(sessionAuthentications);
		try {
			SecurityActions.runWithAuthentications(new SecurityAction<Void>() {
				public Void execute() {
					try {
						chain.doFilter(request, response);
					} 
					catch (Exception e) {
						throw new RuntimeException(e);
					}
					return null;
				}
			}, sessionAuthentications);
		} finally {
			Authentications.clearCurrent();
			// store updated authentication object in session for next request
			Authentications.updateSession(req.getSession(), sessionAuthentications);
		}
	}

	private void addAuthentication(String username, Authentications authentications) {
		final ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();

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
}