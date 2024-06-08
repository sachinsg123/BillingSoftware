package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.billing.model.PartiesTransaction;
import com.billing.model.Supplier;

@Repository
public interface PartiesTransectionRepository extends JpaRepository<PartiesTransaction, Integer> {

	@Query("SELECT s FROM PartiesTransaction s WHERE s.status ='Active'")
	List<PartiesTransaction> showAllActivePartiesTransection();
}
