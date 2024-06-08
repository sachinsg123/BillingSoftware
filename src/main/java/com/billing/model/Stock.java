package com.billing.model;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Stock {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private Date date;
	
	private String quantity;
	
	private String minQuantity;

	private String taxInPercentage;
	
	@OneToOne(mappedBy = "stock")
	private Product product;

	public String getTaxInPercentage() {
		return taxInPercentage;
	}

	public void setTaxInPercentage(String taxInPercentage) {
		this.taxInPercentage = taxInPercentage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(String minQuantity) {
		this.minQuantity = minQuantity;
	}
	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
	

}
