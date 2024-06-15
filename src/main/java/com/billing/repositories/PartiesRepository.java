package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.model.Parties;

public interface PartiesRepository extends JpaRepository<Parties, Integer>{
	
	@Query("SELECT s FROM Parties s JOIN s.user u WHERE s.status = 'Active' AND u.id = :id")
	List<Parties> showAllActiveParties(@Param("id") Integer id);
}
