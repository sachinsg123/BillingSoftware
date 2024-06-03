package com.billing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.billing.model.PartiesTransaction;

@Repository
public interface PartiesTransectionRepository extends JpaRepository<PartiesTransaction, Integer> {

}
