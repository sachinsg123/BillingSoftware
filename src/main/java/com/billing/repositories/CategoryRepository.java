package com.billing.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.billing.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	public Category findByCategoryName(String categoryName);
	
	@Query("SELECT c FROM Category c WHERE c.status = 'Active'")
	public List<Category> findByActiveCategory();

}
