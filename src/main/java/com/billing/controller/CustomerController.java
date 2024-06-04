package com.billing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.billing.model.Company;
import com.billing.model.Customer;
import com.billing.model.User;
import com.billing.repositories.BrandRepository;
import com.billing.repositories.CategoryRepository;
import com.billing.repositories.ColorRepository;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.GSTRepository;
import com.billing.repositories.ProductRepository;
import com.billing.repositories.SizeRepository;
import com.billing.repositories.SupplierRepository;
import com.billing.repositories.UnitRepository;
import com.billing.repositories.UserRepository;
import com.billing.services.CustomerServiceImpl;
import com.billing.services.ProductServiceImpl;
import com.billing.services.SupplierServiceImpl;
import com.billing.utils.StringUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/a2zbilling/admin")
public class CustomerController {

	@Autowired
	private ProductRepository productRepo;

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private ColorRepository colorRepo;

	@Autowired
	private SizeRepository sizeRepo;

	@Autowired
	private ProductServiceImpl productService;

	@Autowired
	private CustomerServiceImpl customerService;

	@Autowired
	private SupplierServiceImpl supplierService;

	@Autowired
	private SupplierRepository supplierRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CompanyRepository companyRepo;

	@Autowired
	private UnitRepository unitRepo;

	@Autowired
	private GSTRepository gstRepo;

	@Autowired
	private BrandRepository brandRepo;

	
	
	@GetMapping("/customer/add")
	public String customerAddForm(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		// Code to Render admin on our page
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
	    	String image = user.getImageUrl();
	    	imgpath = StringUtils.ImagePaths.userImageUrl + image;
	    }

		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "/admin/add_customer_form";

	}

	@PostMapping("/customer/add")
	public String addingProcessCustomer(@ModelAttribute Customer customer, Model model, HttpSession session) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user1 = userRepo.findByUsername(auth.getName());

		String user = auth.getName();

		User addedByUser = userRepo.findByUsername(user);

		List<User> userList = new ArrayList<User>();
		userList.add(addedByUser);

		customer.setUser(userList);

		customer.setStatus("Active");

		Customer customer2 = customerService.addCustomer(customer);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";

		if(user1.getImageUrl() != null && !user1.getImageUrl().isEmpty())
	    {
	    	String image = user1.getImageUrl();
	    	imgpath = StringUtils.ImagePaths.userImageUrl + image;
	    }
		model.addAttribute("imagePath", imgpath);

		session.setAttribute("message", "Customer Added Successfully");

		return "redirect:/a2zbilling/admin/customer/add";

	}

	// Get all customers
	@GetMapping("/customer/list")
	public String getAllCustomers(Model model) {

//		List<Customer> customers = customerService.getAllCustomers();
		List<Customer> activeCustomers = customerService.getActiveCustomers();
		model.addAttribute("customers", activeCustomers);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
	    	String image = user.getImageUrl();
	    	imgpath = StringUtils.ImagePaths.userImageUrl + image;
	    }

		model.addAttribute("imagePath", imgpath);

		return "admin/customer_list";

	}

	// update customer form
	@GetMapping("/customer/update/{id}")
	public String updateCustomerDetails(@PathVariable("id") Integer customerId, Model model) {

		Customer customerGet = customerService.getCustomerById(customerId);
		model.addAttribute("customer", customerGet);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
	    	String image = user.getImageUrl();
	    	imgpath = StringUtils.ImagePaths.userImageUrl + image;
	    }

		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "/admin/update_customer";

	}

	@PostMapping("/customer/update")
	public String customerUpdateProcessing(@ModelAttribute Customer customer, Model model, HttpSession session) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		Optional<Customer> customerFound = customerRepo.findById(customer.getId());
		Customer customerGet = customerFound.get();
		customerGet.setName(customer.getName());
		customerGet.setEmail(customer.getEmail());
		customerGet.setMobile(customer.getMobile());
		customerGet.setAddress(customer.getAddress());

		if (!customer.getAddedDate().isEmpty()) {

			customerGet.setAddedDate(customer.getAddedDate());
		}
		customerGet.setAddedDate(customerGet.getAddedDate());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
	    	String image = user.getImageUrl();
	    	imgpath = StringUtils.ImagePaths.userImageUrl + image;
	    }

		model.addAttribute("imagePath", imgpath);

		customerRepo.save(customerGet);

		return "redirect:/a2zbilling/admin/customer/list";

	}
	// delete customer
	@GetMapping("/customer/delete/{id}")
	public String deleteCustomerById(@PathVariable("id") int id) {

		Customer customerFetched = customerService.getCustomerById(id);

		customerFetched.setStatus("inActive");
		customerRepo.save(customerFetched);

		return "redirect:/a2zbilling/admin/customer/list";
	}
	
}
