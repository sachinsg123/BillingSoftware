package com.billing.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.billing.model.Product;
public interface ProductServices{
	
	public Product addProduct(Product product);
//	public List<Product> getAvailableProducts();
	public List<Product> getAllProducts();
	public Product getSingleProduct(int id);
	Page<Product> getAvailableProducts(int page, int size);
	
	

}
