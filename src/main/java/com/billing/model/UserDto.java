package com.billing.model;

import org.springframework.web.multipart.MultipartFile;

//created by Mahesh
public class UserDto {
	
	private String username;
	
	private String email;
	
	private String mobile;
	
	private String password;
	
	private MultipartFile imageUrl;
	
	private String companyname;

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public MultipartFile getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(MultipartFile imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
