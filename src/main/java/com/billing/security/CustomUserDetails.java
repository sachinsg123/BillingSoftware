package com.billing.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.billing.model.User;

import io.micrometer.common.util.StringUtils;

public class CustomUserDetails implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	
	private User user;
	
	public CustomUserDetails(User user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
		List<SimpleGrantedAuthority> roles = Arrays.asList(authority);
		return roles;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		String username = (!StringUtils.isEmpty(user.getUsername())) ? user.getUsername() : user.getEmail();
		return username;
	}
	
	public String getEmail() {
		
		return this.user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

}
