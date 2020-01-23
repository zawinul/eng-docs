package it.eng.ms.restservice.security;

import it.eng.ms.restservice.ContextService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtFilterInstaller {
	@Autowired
	private ContextService cfg;


	@Bean
	public FilterRegistrationBean<JwtFilter> loggingFilter(){
		
	    FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
	    
	    String filterPath = cfg.getProperty("authenticationFilterPath");
	    if (filterPath==null)
	    	filterPath = "/api/*";
	    
	    JwtFilter filter = cfg.getContext().getBean(JwtFilter.class);
	    registrationBean.setFilter(filter);
	    registrationBean.addUrlPatterns(filterPath);
	         
	    return registrationBean;    
	}

}