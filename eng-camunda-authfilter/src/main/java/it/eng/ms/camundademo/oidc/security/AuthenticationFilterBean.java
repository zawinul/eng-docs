package it.eng.ms.camundademo.oidc.security;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthenticationFilterBean  {
	@Bean
	public FilterRegistrationBean authenticationFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		
		//Filter myFilter = new EngOidcAuthenticationFilter();
		Filter myFilter = new OidcAuthenticationFilter();
		
		registration.setFilter(myFilter);
		registration.addUrlPatterns("/*");
		//registration.addInitParameter("authentication-provider","org.camunda.bpm.engine.rest.security.auth.impl.HttpBasicAuthenticationProvider");
		registration.setName("camunda-auth");
		registration.setOrder(1);
		return registration;
	}
}