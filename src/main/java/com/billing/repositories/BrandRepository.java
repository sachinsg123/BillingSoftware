package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.model.Brand;
import com.billing.model.Parties;
public interface BrandRepository extends JpaRepository<Brand, Integer>{
	
	public Brand findByName(String brandName);
	
	@Query("SELECT b FROM Brand b JOIN b.user u WHERE b.status = 'Active' AND u.id = :id")
	List<Brand> showAllActiveBrand(@Param("id") Integer id);
}
