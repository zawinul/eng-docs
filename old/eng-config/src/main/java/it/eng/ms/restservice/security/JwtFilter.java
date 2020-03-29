package it.eng.ms.restservice.security;

import it.eng.ms.restservice.CacheHelper;
import it.eng.ms.restservice.ContextService;
import it.eng.ms.restservice.exception.ForbiddenException;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.jwt.JwtClaims;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private ContextService cfg;

	private final String BEARER_PREFIX = "Bearer ";
	
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        
		boolean disabled = "true".equals(cfg.getProperty("debugBypassAuthorization"));
		
		addCORS(request, response);
    	//System.out.println(request.getRequestURI());
    	
    	// per le richieste "public/*" non serve il token
    	if (request.getRequestURI().startsWith("/public/")) {
	        chain.doFilter(request, response);
	        return;
    	}
    	
    	if (disabled) {
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
    
    private void addCORS(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, secret");
        response.setHeader("Access-Control-Max-Age", "3600");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } 
    }
}