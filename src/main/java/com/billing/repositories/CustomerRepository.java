package com.billing.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	@Query("SELECT c FROM Customer c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	List<Customer> showAllCustomerBYActive(@Param("id") Integer id);
	
	@Query("SELECT c FROM Customer c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	Page<Customer> showAllCustomerBYActive(@Param("id") Integer id, Pageable pageable);
	
	public Customer findByName(String name);
}
