package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.model.Charges;

public interface ChargesRepository extends JpaRepository<Charges, Integer> {

	@Query("SELECT c FROM Charges c JOIN c.user u WHERE u.id = :id")
	public List<Charges> findByActiveCharges(@Param("id") Integer id);
	
	Charges findByName(String name);
}
