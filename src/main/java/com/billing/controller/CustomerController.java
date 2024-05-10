package com.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.billing.model.Customer;
import com.billing.services.CustomerServiceImpl;

@Controller
@RequestMapping("")
public class CustomerController {

	@Autowired
	private CustomerServiceImpl customerService;
	
	@GetMapping("/customerDetails/{id}")
	public Customer getCustomerDetailsByID(Model model, @PathVariable("id") int id) {
	    Customer customer = customerService.getCustomerById(id);
	    
	    System.out.println(customer.getName()+" "+customer.getEmail());
	    
	    model.addAttribute("customer",customer);
	    
	    return customer;
	}
}
