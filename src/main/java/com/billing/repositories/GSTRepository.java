package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billing.model.GSTRate;

@Repository
public interface GSTRepository extends JpaRepository<GSTRate, Integer>{
	
	public GSTRate findBygstRate(String gstRate);

}

