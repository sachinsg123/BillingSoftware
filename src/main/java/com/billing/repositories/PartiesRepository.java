package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.billing.model.Parties;

public interface PartiesRepository extends JpaRepository<Parties, Integer>{
	
	@Query("SELECT s FROM Parties s WHERE s.status ='Active'")
	List<Parties> showAllActiveParties();
}
