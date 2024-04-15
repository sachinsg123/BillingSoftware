package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billing.model.Color;

@Repository
public interface ColorRepository extends JpaRepository<Color, Integer>{
	
		public Color findByName(String colorName);
}
