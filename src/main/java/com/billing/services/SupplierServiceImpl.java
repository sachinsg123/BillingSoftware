package com.billing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billing.model.Supplier;
import com.billing.repositories.SupplierRepository;

@Service
public class SupplierServiceImpl implements SupplierService {

	// Changes by Younus
	@Autowired
	private SupplierRepository supplierRepo;

	@Override
	public List<Supplier> getAllSupplierAddedByAdmin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Supplier updateSupplier(Supplier supplier) {

		return null;
	}

	// Created by Younus
	@Override
	public long getSupplierCount() {
		int count = 0;
		List<Supplier> suppliers = supplierRepo.findAll();
		for (Supplier sup : suppliers) {
			if (sup.getStatus().equals("Active")) {
				count++;
			}
		}
		return count;
	}

}
