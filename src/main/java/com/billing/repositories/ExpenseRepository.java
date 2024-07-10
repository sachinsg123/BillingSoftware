package com.billing.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.billing.model.Customer;
import com.billing.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
	
	@Query("SELECT c FROM Expense c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	List<Expense> showAllActiveExpenseList(@Param("id") Integer id);
	
	@Query("SELECT c FROM Expense c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	Page<Expense> showAllActiveExpenseList(@Param("id") Integer id, Pageable pageable);
	
	@Query("SELECT CONCAT('EB - ', MAX(CAST(SUBSTRING(c.billNo, 5) AS integer))) FROM Expense c JOIN c.user u WHERE c.status = 'Active' AND u.id = :id")
	String maxExpenseBillNo(@Param("id") Integer id);

}
