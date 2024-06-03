package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.billing.model.Sales;

public interface SalesRepository extends JpaRepository<Sales, Integer> {
	@Query("SELECT c FROM Sales c WHERE c.status ='Active'")
	List<Sales> showAllActiveSales();
}
