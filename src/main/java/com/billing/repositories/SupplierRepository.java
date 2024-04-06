package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.billing.model.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

	@Query("SELECT s FROM Supplier s WHERE s.status ='Active'")
	List<Supplier> showAllActiveSupplier();
}
