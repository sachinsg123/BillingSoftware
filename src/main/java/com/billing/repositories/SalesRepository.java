package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.model.Sales;

public interface SalesRepository extends JpaRepository<Sales, Integer> {
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	Page<Sales> showAllActiveSales(@Param("id") Integer id, Pageable pageable);
	
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	List<Sales> showAllActiveSales(@Param("id") Integer id);
}
