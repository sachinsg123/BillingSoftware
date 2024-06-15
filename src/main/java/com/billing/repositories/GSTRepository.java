package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.GSTRate;

@Repository
public interface GSTRepository extends JpaRepository<GSTRate, Integer>{
	
	@Query("SELECT s FROM GSTRate s JOIN s.user u WHERE u.id = :id")
	List<GSTRate> showAllActiveGST(@Param("id") Integer id);
	
	public GSTRate findBygstRate(String gstRate);

}

