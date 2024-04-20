package com.billing.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.billing.model.Customer;
import com.billing.repositories.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private CustomerRepository customerRepo;

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
		
		List<Customer> customers = customerRepo.showAllCustomerBYActive();
		
		return customers;
		
	}

	//Changes by Younus
	@Override
	public long getCustomerCount() {
		int count=0;
		
	List<Customer> customers=customerRepo.findAll();
	for(Customer customer : customers) {
		if(customer.getStatus().equals("Active")) {
			count++;
		}
	}
		return count;
	}

}
