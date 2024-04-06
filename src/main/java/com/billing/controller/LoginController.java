package com.billing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class LoginController{
	
	@GetMapping("/login-user")
	public String loginPage(){
		
		return "/users/loginform";
		
	}
	
		
		
	}


