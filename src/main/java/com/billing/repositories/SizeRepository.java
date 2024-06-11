package com.billing.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.Size;
import com.billing.model.Supplier;

@Repository
public interface SizeRepository extends JpaRepository<Size, Integer>{

	@Query("SELECT s FROM Size s JOIN s.user u WHERE u.id = :id")
	List<Size> showAllSize(@Param("id") Integer id);
	
	public Size findBySizeValue(String sizeName);
}
