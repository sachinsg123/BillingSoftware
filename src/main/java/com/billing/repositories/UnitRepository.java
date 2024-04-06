package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.billing.model.Unit;

@Service
public interface UnitRepository extends JpaRepository<Unit, Integer>{
	
	
	public Unit findByunitName(String unitName);
	

}
