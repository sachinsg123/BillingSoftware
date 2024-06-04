package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.billing.model.Brand;
import com.billing.model.Parties;
public interface BrandRepository extends JpaRepository<Brand, Integer>{
	
	public Brand findByName(String brandName);
	
	@Query("SELECT s FROM Brand s WHERE s.status ='Active'")
	List<Brand> showAllActiveBrand();
}
