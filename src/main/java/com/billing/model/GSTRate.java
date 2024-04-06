package com.billing.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class GSTRate{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String gstRate;
	
	private String cgstRate;
	
	private String sgstRate;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToMany(mappedBy = "gst", cascade = CascadeType.ALL)
	private List<Product> products = new ArrayList<Product>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGstRate() {
		return gstRate;
	}

	public void setGstRate(String gstRate) {
		this.gstRate = gstRate;
	}

	public String getCgstRate() {
		return cgstRate;
	}

	public void setCgstRate(String cgstRate) {
		this.cgstRate = cgstRate;
	}

	public String getSgstRate() {
		return sgstRate;
	}

	public void setSgstRate(String sgstRate) {
		this.sgstRate = sgstRate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public String toString() {
		return "GSTRate [id=" + id + ", gstRate=" + gstRate + ", cgstRate=" + cgstRate + ", sgstRate=" + sgstRate
				+ ", user=" + user + ", products=" + products + "]";
	}
	
	 
	
	
	

}
