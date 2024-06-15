package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	public Category findByCategoryName(String categoryName);
	
	@Query("SELECT c FROM Category c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	public List<Category> findByActiveCategory(@Param("id") Integer id);
	
	@Query("SELECT c FROM Category c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	public Page<Category> findByActiveCategory(@Param("id") Integer id, Pageable pageable);


}
