package com.billing.services;

import java.util.List;

import com.billing.model.Product;
public interface ProductServices{
	
	public Product addProduct(Product product);
	public List<Product> getAvailableProducts();
	public List<Product> getAllProducts();
	public Product getSingleProduct(int id);
	
	

}
