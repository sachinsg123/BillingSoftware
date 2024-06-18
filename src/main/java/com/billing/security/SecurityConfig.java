package com.billing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.filter.OncePerRequestFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
	@Autowired
	private CustomUserDetailService customUserDetailService;
	
	@Autowired
	private CustomSuccessHandler customSuccessHandler;
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new CustomUserDetailService();
	}
	
	@Bean
	public DaoAuthenticationProvider getDaoAuthenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(getUserDetailsService());
		authenticationProvider.setPasswordEncoder(getpasswordEncoder());
		return authenticationProvider;
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http
		.csrf().disable()
		.authorizeHttpRequests()
		.requestMatchers("/a2zbilling/admin/**")
		.hasAnyAuthority("ADMIN")
		.requestMatchers("/a2zbilling/user/**")
		.hasAuthority("USER")
		.requestMatchers("/a2zbilling/home","/css","/static","/js","/**")
		.permitAll()
		.and()
		.formLogin()
		.loginPage("/auth/login-user")
		.loginProcessingUrl("/signin")
		.successHandler(customSuccessHandler)
		.permitAll()
		.and()
		.logout()
		.logoutUrl("/logout")
		.and()
		.requestCache() .requestCache(new HttpSessionRequestCache())
		;
		return http.build();
	}

    @Bean
    PasswordEncoder getpasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
    
    

}
