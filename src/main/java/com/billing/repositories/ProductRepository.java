package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.billing.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query("select p from Product p where p.status ='Active'")
	public Page<Product> getAllProductByStatus(Pageable pageable);

}
