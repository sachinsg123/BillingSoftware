package com.billing.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.billing.model.User;
import com.billing.repositories.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepo.findByEmail(username);
		
		if(user == null){
			
			throw new UsernameNotFoundException("user not found this email or password");
		}
		
		System.out.println("custom user details : "+user.getEmail()+" "+user.getPassword()+" "+user.getRole());
		
		/* Collection<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());
		
		System.out.println(authorities);*/
		
		return  new CustomUserDetails(user);
				
				
		
		
	}

}
