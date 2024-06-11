package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.billing.model.Supplier;
import com.billing.model.Unit;

@Service
public interface UnitRepository extends JpaRepository<Unit, Integer>{
	
	@Query("SELECT s FROM Unit s JOIN s.user u WHERE u.id = :id")
	List<Unit> showAllActiveUnit(@Param("id") Integer id);
	
	public Unit findByunitName(String unitName);
	

}
