package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billing.model.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer>{

	
	public Size findBySizeValue(String sizeName);
}
