package it.eng.ms.camundademo.security;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationProvider;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngProvider implements AuthenticationProvider {

	@Override
	public void augmentResponseByAuthenticationChallenge(HttpServletResponse request, ProcessEngine engine) {
		mylog("augmentResponseByAuthenticationChallenge");
	}

	@Override
	public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine) {
		mylog("extractAuthenticatedUser");
		return null;
	}
	
	private void mylog(String x) {
		System.out.println(x);
		
	}
}
