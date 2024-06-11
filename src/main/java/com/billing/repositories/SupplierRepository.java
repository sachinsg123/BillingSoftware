package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

	@Query("SELECT s FROM Supplier s JOIN s.user u WHERE s.status = 'Active' AND u.id = :id")
	List<Supplier> showAllActiveSupplier(@Param("id") Integer id);
}
