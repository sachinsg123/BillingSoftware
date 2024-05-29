package com.billing.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_company")
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "company_name")
	private String name;

	private String phone;

	private String email;

	private String Address;

	@Column(name = "company_type")
	private String type;

	@OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
	private BusinessCategory businessCategory;

	private String gstn;

	private String signature;

	private String state;

	private String description;

	private String logo;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

//	private List<Customer> customers = new ArrayList<Customer>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	//for Company Name
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BusinessCategory getBusinessCategory() {
		return businessCategory;
	}

	public void setBusinessCategory(BusinessCategory businessCategory) {
		this.businessCategory = businessCategory;
	}

	public String getGstn() {
		return gstn;
	}

	public void setGstn(String gstn) {
		this.gstn = gstn;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	@Override
	public String toString() {
		return "Company [id=" + id + ", name=" + name + ", phone=" + phone + ", email=" + email + ", Address=" + Address
				+ ", type=" + type + ", businessCategory=" + businessCategory + ", gstn=" + gstn + ", signature="
				+ signature + ", state=" + state + ", description=" + description + ", logo=" + logo + ", user=" + user
				+ "]";
	}

	/*
	 * public List<Customer> getCustomers() { return customers; }
	 * 
	 * public void setCustomers(List<Customer> customers) { this.customers =
	 * customers; }
	 * 
	 */

}
