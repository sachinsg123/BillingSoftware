package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.billing.model.Brand;
public interface BrandRepository extends JpaRepository<Brand, Integer>{
	
	public Brand findByName(String brandName)
;
}
