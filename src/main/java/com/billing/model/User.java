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
	
	@OneToMany(mappedBy = "user")
	private List<Product> products = new ArrayList<Product>();

	@OneToMany(mappedBy ="user")
	private List<Category> categories = new ArrayList<Category>();
	
	@OneToMany(mappedBy ="user")
	private List<Charges> charges = new ArrayList<Charges>();
	
	@OneToMany(mappedBy ="user")
	private List<Brand> brand = new ArrayList<Brand>();
	
	@OneToMany(mappedBy ="user")
	private List<Parties> parties = new ArrayList<Parties>();
	
	@OneToMany(mappedBy ="user")
	private List<PurchaseOrder> purchaseorder = new ArrayList<PurchaseOrder>();
	
	

	public List<Charges> getCharges() {
		return charges;
	}

	public void setCharges(List<Charges> charges) {
		this.charges = charges;
	}

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
	
	@OneToMany(mappedBy = "user")
	private List<Size> sizes = new ArrayList<Size>();
	
	@OneToOne(mappedBy = "user")
	private Company company;
	
	@OneToMany(mappedBy = "user")
	private List<GSTRate> gst = new ArrayList<GSTRate>();
	
	@OneToMany(mappedBy = "user")
	private List<Unit> units = new ArrayList<Unit>();
	
	@OneToMany(mappedBy = "user")
	private List<Sales> sales = new ArrayList<Sales>();

	@OneToMany(mappedBy = "user")
	private List<PartiesTransaction> partiesTransactions = new ArrayList<PartiesTransaction>();

	
	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public List<PartiesTransaction> getPartiesTransactions() {
		return partiesTransactions;
	}

	public void setPartiesTransactions(List<PartiesTransaction> partiesTransactions) {
		this.partiesTransactions = partiesTransactions;
	}

	public List<Sales> getSales() {
		return sales;
	}

	public void setSales(List<Sales> sales) {
		this.sales = sales;
	}

	public List<Size> getSizes() {
		return sizes;
	}

	public void setSizes(List<Size> sizes) {
		this.sizes = sizes;
	}

	public List<Brand> getBrand() {
		return brand;
	}

	public void setBrand(List<Brand> brand) {
		this.brand = brand;
	}

	public List<Parties> getParties() {
		return parties;
	}

	public void setParties(List<Parties> parties) {
		this.parties = parties;
	}

	public List<GSTRate> getGst() {
		return gst;
	}

	public void setGst(List<GSTRate> gst) {
		this.gst = gst;
	}

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
	public List<PurchaseOrder> getPurchaseorder() {
		return purchaseorder;
	}

	public void setPurchaseorder(List<PurchaseOrder> purchaseorder) {
		this.purchaseorder = purchaseorder;
	}
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
