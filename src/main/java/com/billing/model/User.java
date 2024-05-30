package com.billing.model;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="User")
public class User{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="userId")
	private int id;
	
	private String username;
	
	private String email;
	
	private String mobile;
	
/*	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name ="user_role",
			joinColumns = @JoinColumn(name="user_id"),
			inverseJoinColumns = @JoinColumn(name="role_id"))
	private List<Roles> roles = new ArrayList<Roles>();  */
	
	@OneToMany(mappedBy = "user")
	private List<Product> products = new ArrayList<Product>(); 


	@OneToMany(mappedBy ="user")
	private List<Category> categories = new ArrayList<Category>(); 
	
	@Column(name="user_role")
	private String role;
	
	@Column(name="userpass")
	private String password;
	
	@Column(name="userImage")
	private String imageUrl;
	
	private String status;
	
	@ManyToMany(mappedBy ="user")
	private List<Customer> customers = new ArrayList<Customer>();
	
	@OneToMany(mappedBy = "user")
	private List<Supplier> supplier = new ArrayList<Supplier>();
	
	@OneToOne(mappedBy = "user")
	private Company company;
	
	@OneToMany(mappedBy = "user")
	private List<GSTRate> gst = new ArrayList<GSTRate>();
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
/*
	public List<Roles> getRoles() {
		return roles;
	}

	public void setRoles(List<Roles> roles) {
		this.roles = roles;
	}
	
	*/	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
	
	

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	


	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<Supplier> getSupplier() {
		return supplier;
	}

	public void setSupplier(List<Supplier> supplier) {
		this.supplier = supplier;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", mobile=" + mobile + "Role="+ role + "]";
	}

	
	

}
