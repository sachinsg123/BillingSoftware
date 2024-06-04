package com.billing.model;

import org.springframework.web.multipart.MultipartFile;

public class BrandDto {
	private int id;
	private String name;
	
	private MultipartFile logo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MultipartFile getLogo() {
		return logo;
	}

	public void setLogo(MultipartFile logo) {
		this.logo = logo;
	}
	

}
