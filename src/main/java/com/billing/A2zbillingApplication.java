package com.billing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class A2zbillingApplication implements CommandLineRunner{
	
	@Autowired	
    PasswordEncoder passwordEncoder;
	

	public static void main(String[] args){
		
		SpringApplication.run(A2zbillingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception{
		String password = "1234";
		String encodedPass = passwordEncoder.encode(password);
		
		System.out.println(encodedPass);
		
		
	}

}
