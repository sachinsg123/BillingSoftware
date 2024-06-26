package com.billing.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.billing.model.Customer;
import com.billing.model.User;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.UserRepository;

@Service
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public Customer addCustomer(Customer customer) {
		
		if(customer.getAddedDate() == null) {
			
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String formatedDate =  formatter.format(date);
		    customer.setAddedDate(formatedDate);
		}
		
		Customer savedCustomer = customerRepo.save(customer);
		
		
		return customer;
	}

	@Override
	public Customer getCustomerByAdmin(String adminName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<Customer> getAllCustomers(){
		
		List<Customer> customers = customerRepo.findAll();
		
		return customers;
	}

	@Override
	public Customer getCustomerById(int id) {
		Customer customer = customerRepo.findById(id).get();
		
		return customer;
	}

	@Override
	public Customer updateCustomer(Customer customer) {
		
		Customer  customerGet =customerRepo.findById(customer.getId()).get();
		customerGet.setName(customer.getName());
		customerGet.setEmail(customer.getEmail());
		customerGet.setMobile(customer.getMobile());
		customerGet.setAddress(customer.getAddress());
		
		if(!customer.getAddedDate().isEmpty()) {
			
			customerGet.setAddedDate(customer.getAddedDate());
		}
		customerGet.setAddedDate(customerGet.getAddedDate());
		
		customerRepo.save(customerGet);
		
		return customerGet ;
	}
	
	public List<Customer> getActiveCustomers(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		
		List<Customer> customers = customerRepo.showAllCustomerBYActive(userId);
		
		return customers;
		
	}

	//Created by Younus
	@Override
	public long getCustomerCount() {
		int count=0;
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		
		List<Customer> customers = customerRepo.showAllCustomerBYActive(userId);
		count = customers.size();
		return count;
	}

}
