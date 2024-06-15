package com.billing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.billing.model.Supplier;
import com.billing.model.User;
import com.billing.repositories.SupplierRepository;
import com.billing.repositories.UserRepository;

@Service
public class SupplierServiceImpl implements SupplierService {

	// Changes by Younus
	@Autowired
	private SupplierRepository supplierRepo;
	
	@Autowired
	private UserRepository userRepo;
	
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		
		int count = 0;
		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier(userId);
		count = suppliers.size();
		
		return count;
	}

}
