package com.billing.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.billing.model.PartiesTransaction;
import com.billing.model.Sales;

public interface SalesRepository extends JpaRepository<Sales, Integer> {
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	Page<Sales> showAllActiveSales(@Param("id") Integer id, Pageable pageable);
	
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND c.salesType = 'Sale' AND u.id = :id")
	Page<Sales> showAllActive(@Param("id") Integer id, Pageable pageable);
	
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND c.salesType = 'Return' AND u.id = :id")
	Page<Sales> showAllActiveSalesReturn(@Param("id") Integer id, Pageable pageable);
	
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	List<Sales> showAllActiveSales(@Param("id") Integer id);
	
	@Query("SELECT CONCAT('SB - ', MAX(CAST(SUBSTRING(c.saleBillNo, 5) AS integer))) FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	String maxSalesBillNo(@Param("id") Integer id);
	

	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND c.paymentMode = 'cash' AND u.id = :id")
	List<Sales> showAllCashPayment(@Param("id") Integer id);
	
	//From date to End Date
	Page<Sales> findByUserIdAndDateBetween(int userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND c.paymentMode = 'online' AND u.id = :id")
	Page<Sales> showAllOnlinePayment(@Param("id") Integer id, Pageable pageable);
	
	@Query("SELECT c FROM Sales c JOIN c.user u WHERE c.status = 'Active' AND c.paymentMode = 'cheque' AND u.id = :id")
	Page<Sales> showAllChequePayment(@Param("id") Integer id, Pageable pageable);

}
