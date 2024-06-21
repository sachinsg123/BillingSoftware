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

@Entity
public class PartiesTransaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int Id;
	
	private String billNo;
	
	private String date;
	
	private String quantity;

	private String discountInRupees;
	
	private String discountInPercentage;
	
	private String taxInRupees;
	
	private String taxInPercentage;
	
	private String paymentMode;
	
	private String paid;
	
	private String dues;
	
	private String totalAmount;
	
	private String status;

	private String netPayment;
	
	private String size;
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@ManyToOne
    @JoinColumn(name = "parties_id")
	private Parties parties;
	
	@ManyToMany
	@JoinTable(
        name = "transaction_product",
        joinColumns = @JoinColumn(name = "parties_transaction_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
	private List<Product> products = new ArrayList<Product>();

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDiscountInRupees() {
		return discountInRupees;
	}

	public void setDiscountInRupees(String discountInRupees) {
		this.discountInRupees = discountInRupees;
	}

	public String getDiscountInPercentage() {
		return discountInPercentage;
	}

	public void setDiscountInPercentage(String discountInPercentage) {
		this.discountInPercentage = discountInPercentage;
	}

	public String getTaxInRupees() {
		return taxInRupees;
	}

	public void setTaxInRupees(String taxInRupees) {
		this.taxInRupees = taxInRupees;
	}

	public String getTaxInPercentage() {
		return taxInPercentage;
	}

	public void setTaxInPercentage(String taxInPercentage) {
		this.taxInPercentage = taxInPercentage;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaid() {
		return paid;
	}

	public void setPaid(String paid) {
		this.paid = paid;
	}

	public String getDues() {
		return dues;
	}

	public void setDues(String dues) {
		this.dues = dues;
	}

	public String getNetPayment() {
		return netPayment;
	}

	public void setNetPayment(String netPayment) {
		this.netPayment = netPayment;
	}

	public Parties getParties() {
		return parties;
	}

	public void setParties(Parties parties) {
		this.parties = parties;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	
}
