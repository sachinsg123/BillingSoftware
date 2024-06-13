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
public class Parties {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private String mobile;
	
	private String partyGroup;
	
	private String billingAddress;

	private String shippingAddress;
	
	private String email;
	
	private String gstType;
	
	private String gstinNumber;
	
	private String state;
	
	private String openingBalance;
	
	private String date;
	
	private String payment;
	
	private String adharNumber;
	
	private String panNumber;
	
	private String drivingLicenceNumber;
	
	private String status;
	
	@OneToMany(mappedBy = "parties", cascade = CascadeType.ALL)
	private List<Product> products = new ArrayList<Product>();
	
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToMany(mappedBy = "parties")
	private List<PartiesTransaction> transactions= new ArrayList<>(); // in that list we store the all transactions of party

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPartyGroup() {
		return partyGroup;
	}

	public void setPartyGroup(String partyGroup) {
		this.partyGroup = partyGroup;
	}

	public String getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGstType() {
		return gstType;
	}

	public void setGstType(String gstType) {
		this.gstType = gstType;
	}

	public String getGstinNumber() {
		return gstinNumber;
	}

	public void setGstinNumber(String gstinNumber) {
		this.gstinNumber = gstinNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getAdharNumber() {
		return adharNumber;
	}

	public void setAdharNumber(String adharNumber) {
		this.adharNumber = adharNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getDrivingLicenceNumber() {
		return drivingLicenceNumber;
	}

	public void setDrivingLicenceNumber(String drivingLicenceNumber) {
		this.drivingLicenceNumber = drivingLicenceNumber;
	}

	public List<PartiesTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<PartiesTransaction> transactions) {
		this.transactions = transactions;
	}
	@Override
	public String toString() {
		return "Parties [id=" + id + ", name=" + name + ", mobile=" + mobile + ", partyGroup=" + partyGroup
				+ ", billingAddress=" + billingAddress + ", shippingAddress=" + shippingAddress + ", email=" + email
				+ ", gstType=" + gstType + ", gstinNumber=" + gstinNumber + ", state=" + state + ", openingBalance="
				+ openingBalance + ", date=" + date + ", payment=" + payment + ", adharNumber=" + adharNumber
				+ ", panNumber=" + panNumber + ", drivingLicenceNumber=" + drivingLicenceNumber + ", status=" + status
				+ ", transactions=" + transactions + "]";
	}
	
}
