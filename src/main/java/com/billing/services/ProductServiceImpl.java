package com.billing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.billing.model.Category;
import com.billing.model.Color;
import com.billing.model.Product;
import com.billing.model.User;
import com.billing.repositories.BrandRepository;
import com.billing.repositories.CategoryRepository;
import com.billing.repositories.ColorRepository;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.ProductRepository;
import com.billing.repositories.SizeRepository;
import com.billing.repositories.StockRepository;
import com.billing.repositories.UserRepository;

@Service
public class ProductServiceImpl implements ProductServices {
	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private StockRepository stockRepo;

	@Autowired
	private BrandRepository brandRepo;

	@Autowired
	private ColorRepository colorRepo;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private SizeRepository sizeRepository;

	@Autowired
	private ProductRepository productRepo;

	@Override
	public Product addProduct(Product product) {

		System.out.println("service working");

		product.setCustomer(product.getCustomer());

		System.out.println(product);

		productRepo.save(product);

		return product;

	}

	@Override  // here i have applied pagination in there 	
	public Page<Product> getAvailableProducts(int page, int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		Pageable pageable =  PageRequest.of(page, size);
	     
		return productRepo.getAllProductByStatus(userId,pageable);
		
	}

	@Override
	public List<Product> getAllProducts() {

		List<Product> allProducts = productRepo.findAll();

		return allProducts;
	}

	@Override
	public Product getSingleProduct(int id) {
		// TODO Auto-generated method stub
		return null;
	}

}
