package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.PartiesTransaction;
import com.billing.model.Supplier;

@Repository
public interface PartiesTransectionRepository extends JpaRepository<PartiesTransaction, Integer> {

	@Query("SELECT s FROM PartiesTransaction s JOIN s.user u WHERE s.status = 'Active' AND u.id = :id")
	List<PartiesTransaction> showAllActivePartiesTransection(@Param("id") Integer id);
	
	@Query("SELECT s FROM PartiesTransaction s JOIN s.user u WHERE s.status = 'Active' AND u.id = :id")
	Page<PartiesTransaction> showAllActivePartiesTransection(@Param("id") Integer id, Pageable pageable);
}
