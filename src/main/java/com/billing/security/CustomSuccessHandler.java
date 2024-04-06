package com.billing.security;

import java.io.IOException;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException{
		
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		
		
System.out.println(roles);
        
        if (roles.isEmpty()) {
        	
            response.sendRedirect("/a2zbilling/public/");
            
        } else if (roles.contains("ADMIN")){
        	
        	System.out.println(roles);
        	response.sendRedirect("/a2zbilling/admin/");
            
        }
	    else if (roles.contains("CUSTOMER")) {
        	System.out.println(roles);
            response.sendRedirect("/a2zbilling/customer/");
            
        } 
	    else {
		   
	    	System.out.println("not working");
             response.sendRedirect("/auth/login-user");
        }

		
	}

}
