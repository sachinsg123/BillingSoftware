package com.billing.services;

import com.billing.model.Customer;

public interface CustomerService {
	
	public Customer addCustomer(Customer customer);
	public Customer getCustomerByAdmin(String adminName);
	public Customer getCustomerById(int id);
	public Customer updateCustomer(Customer customer);
	
	//Changes by Younus
	public long getCustomerCount();

}
