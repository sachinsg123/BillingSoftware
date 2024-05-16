package com.billing.services;

import java.util.Optional;

import com.billing.model.Customer;

public interface CustomerService {
	
	public Customer addCustomer(Customer customer);
	public Customer getCustomerByAdmin(String adminName);
	public Customer getCustomerById(int id);
	public Optional<Customer> getCustomerByName(String name);
	public Customer updateCustomer(Customer customer);
	
	//Changes by Younus
	public long getCustomerCount();

}
