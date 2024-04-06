package com.billing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/a2zbilling/public")
public class PublicController{
	
	@GetMapping("/")
	public String publicHomePage() {
		
		return "/public/homep";
	}
	
	

}
