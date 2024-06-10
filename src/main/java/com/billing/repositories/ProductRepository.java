package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	
	@Query("select p from Product p JOIN p.user u WHERE p.status = 'Active' AND u.id = :id")
	public Page<Product> getAllProductByStatus(@Param("id") Integer id,Pageable pageable);
	
	@Query("select p from Product p JOIN p.user u WHERE p.status = 'Active' AND u.id = :id")
	public List<Product> showAllActiveProduct(@Param("id") Integer id);
}
