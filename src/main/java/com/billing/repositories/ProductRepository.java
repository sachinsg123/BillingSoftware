package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billing.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
