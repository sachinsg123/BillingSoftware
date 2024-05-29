package com.billing.services;

import java.util.List;

import com.billing.model.Supplier;

public interface SupplierService {
	
	public List<Supplier> getAllSupplierAddedByAdmin();
	
	public Supplier updateSupplier(Supplier supplier);
	
//	//Created by Younus
	public long getSupplierCount();
}
