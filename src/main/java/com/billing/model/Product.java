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
import jakarta.persistence.OneToOne;

@Entity
public class Product{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String name;
	
	private String quantity;
	
	@ManyToOne
	@JoinColumn(name="color_id")
	private Color color;
	
	private String price;

	private String sellingPrice;
	
	@ManyToOne
	@JoinColumn(name="brand_id")
	private Brand brand;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	private String addedDate;
	
	private String status;
	
	private String imageUrl;
	
	@ManyToMany
	@JoinTable(name="product_customer",
	joinColumns = @JoinColumn(name="product_id")
	,inverseJoinColumns = @JoinColumn(name="customer_id"))
	private List<Customer> customer = new ArrayList<Customer>();
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category ;
	
    @OneToOne
    @JoinColumn(name="stock_id")
	private Stock stock;
    
    @ManyToOne
    @JoinColumn(name="size_id")
    private Size size;
    
    
    private String about;
    
//    @ManyToOne
//    @JoinColumn(name="supplier_id")
//    private Supplier supplier;
    
    @ManyToOne
    @JoinColumn(name="parties_id")
    private Parties parties;
	
    public String getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(String sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public Parties getParties() {
		return parties;
	}

	public void setParties(Parties parties) {
		this.parties = parties;
	}

	@ManyToOne
    @JoinColumn(name="unit_id")
    private Unit unit;
	
//	@ManyToOne
//    @JoinColumn(name="purchaseorder_id")
//    private PurchaseOrder purchaseOrder;
	
	@ManyToMany(mappedBy = "products")
    private List<PurchaseOrder> purchaseorder = new ArrayList<>();
    
    public List<PurchaseOrder> getPurchaseorder() {
		return purchaseorder;
	}

	public void setPurchaseorder(List<PurchaseOrder> purchaseorder) {
		this.purchaseorder = purchaseorder;
	}

	@ManyToOne
    @JoinColumn(name="gst_id")
    private GSTRate gst;
    
    @ManyToMany(mappedBy = "products")
    private List<PartiesTransaction> transactions = new ArrayList<>();
    
    @ManyToMany(mappedBy = "products")
    private List<Sales> sales = new ArrayList<>();

	public List<PartiesTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<PartiesTransaction> transactions) {
		this.transactions = transactions;
	}

	public List<Sales> getSales() {
		return sales;
	}

	public void setSales(List<Sales> sales) {
		this.sales = sales;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public GSTRate getGst() {
		return gst;
	}

	public void setGst(GSTRate gst) {
		this.gst = gst;
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

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public String getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<Customer> getCustomer() {
		return customer;
	}

	public void setCustomer(List<Customer> customer) {
		this.customer = customer;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}
	
}
