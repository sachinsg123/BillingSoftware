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
public class Sales {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String saleBillNo;
	
	private String date;
	
	private String quantity;
	
	private String taxInAmount;
	
	private String taxInPercentage;
	
	private String discountInPercentage;
	
	private String discountInAmount;
	
	private String paymentMode;
	
	private String amountPaid;
	
	private String dueAmount;
	
	private String netPayment;
	
	private String totalAmount;
	
	private String SignatureImage;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToMany
	@JoinTable(name="sales_product",
	joinColumns = @JoinColumn(name="sales_id")
	,inverseJoinColumns = @JoinColumn(name="product_id"))
	private List<Product> products = new ArrayList<>();

	private String status;
	
	public String getSignatureImage() {
		return SignatureImage;
	}

	public void setSignatureImage(String SignatureImage) {
		this.SignatureImage = SignatureImage;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSaleBillNo() {
		return saleBillNo;
	}

	public void setSaleBillNo(String saleBillNo) {
		this.saleBillNo = saleBillNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getTaxInAmount() {
		return taxInAmount;
	}

	public void setTaxInAmount(String taxInAmount) {
		this.taxInAmount = taxInAmount;
	}

	public String getTaxInPercentage() {
		return taxInPercentage;
	}

	public void setTaxInPercentage(String taxInPercentage) {
		this.taxInPercentage = taxInPercentage;
	}

	public String getDiscountInPercentage() {
		return discountInPercentage;
	}

	public void setDiscountInPercentage(String discountInPercentage) {
		this.discountInPercentage = discountInPercentage;
	}

	public String getDiscountInAmount() {
		return discountInAmount;
	}

	public void setDiscountInAmount(String discountInAmount) {
		this.discountInAmount = discountInAmount;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}

	public String getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(String dueAmount) {
		this.dueAmount = dueAmount;
	}

	public String getNetPayment() {
		return netPayment;
	}

	public void setNetPayment(String netPayment) {
		this.netPayment = netPayment;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
}
