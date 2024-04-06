package com.billing.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billing.model.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>{

}
