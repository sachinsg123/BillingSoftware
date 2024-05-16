package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer>{
	
	@Query("SELECT c FROM Company c WHERE c.user.id=:id")
	public Company getCompanyByUserId(@Param("id") int id);
	
	public Company findByName(String name);
}
