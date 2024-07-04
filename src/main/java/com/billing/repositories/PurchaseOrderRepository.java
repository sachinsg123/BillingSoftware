package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

	@Query("select p from PurchaseOrder p JOIN p.user u WHERE p.status = 'Active' AND u.id = :id")
	public List<PurchaseOrder> showAllActivePurchaseOrderTransection(@Param("id") Integer id);
	
	//Pagination
	@Query("SELECT s FROM PurchaseOrder s JOIN s.user u WHERE s.status = 'Active' AND u.id = :id")
	Page<PurchaseOrder> showAllActivePurchaseOrderTransection(@Param("id") Integer id, Pageable pageable);
	
	//Generate Bill No
	@Query("SELECT CONCAT('PO - ', MAX(CAST(SUBSTRING(c.orderNo, 5) AS integer))) FROM PurchaseOrder c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	String maxPurchaseOrderNo(@Param("id") Integer id);
}
