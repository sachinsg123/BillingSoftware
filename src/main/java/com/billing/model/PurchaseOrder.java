package com.billing.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class PurchaseOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
//	@OneToMany(mappedBy ="purchaseOrder")
//	private List<Product> product =new ArrayList<Product>();
	
	@ManyToMany
	@JoinTable(
        name = "purchaseorder_product",
        joinColumns = @JoinColumn(name = "purchase_order_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
	private List<Product> products = new ArrayList<Product>();
	
	private String date;
	
	private String quantity;
	
	private String status;
	
	private String orderNo;
	
    private String size;

	@ManyToOne
    @JoinColumn(name="parties_id")
    private Parties parties;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	
	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}


	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getSize() {
		return size;
	}
	
	public void setSize(String size) {
		this.size = size;
	}

	public Parties getParties() {
		return parties;
	}

	public void setParties(Parties parties) {
		this.parties = parties;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

//	public List<Product> getProduct() {
//		return product;
//	}
//
//	public void setProduct(List<Product> product) {
//		this.product = product;
//	}

	
	


}
