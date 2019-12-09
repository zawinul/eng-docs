package it.eng.ms.restservice.security;



import it.eng.ms.restservice.CacheHelper;
import it.eng.ms.restservice.exception.ForbiddenException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.jwt.JwtClaims;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	@Value( "${filterpath}" )
	private String filterPath;

	@Bean
	public FilterRegistrationBean<JwtAuthenticationTokenFilter> loggingFilter(){
		System.out.println("FilterRegistrationBean");
	    FilterRegistrationBean<JwtAuthenticationTokenFilter> registrationBean 
	      = new FilterRegistrationBean<>();
	         
	    registrationBean.setFilter(new JwtAuthenticationTokenFilter());
	    registrationBean.addUrlPatterns(filterPath);
	         
	    CacheHelper.init();
	    return registrationBean;    
	}

	private static 	EngOidcTokenVerifier verifier = null;
	private final String BEARER_PREFIX = "Bearer ";
	
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
    	//System.out.println(request.getRequestURI());
    	
    	// per le richieste "public/*" non serve il token
    	if (request.getRequestURI().startsWith("/public/")) {
	        chain.doFilter(request, response);
	        return;
    	}

    	// estrae il token dall'header della richiesta
    	String authToken = request.getHeader("Authorization");
        if (authToken==null)
        	throw new ForbiddenException("manca l'header Authorization");
        if (!authToken.startsWith(BEARER_PREFIX))
        	throw new ForbiddenException("l'header Authorization non è di tipo Bearer");
        authToken = authToken.substring(BEARER_PREFIX.length());
        
        try {
			request.setAttribute("authToken", authToken);
        	JwtClaims jwtClaims = CacheHelper.tokenToClaims(authToken);
			if (jwtClaims==null) {
				response.setStatus(401); // unauthorized
				response.getWriter().write("unauthorized");
			}
			else {
				request.setAttribute("jwtClaims", jwtClaims);
				request.setAttribute("authToken", authToken);
				JSONObject userInfo = CacheHelper.tokenToUserInfo(authToken);
				request.setAttribute("userInfo", userInfo);

				chain.doFilter(request, response);
			}
		} 
        catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}

    }
}