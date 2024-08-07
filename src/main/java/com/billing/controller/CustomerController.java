package com.billing.controller;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.billing.model.Company;
import com.billing.model.Customer;
import com.billing.model.User;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.UserRepository;
import com.billing.services.CustomerServiceImpl;
import com.billing.utils.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/a2zbilling/admin")
public class CustomerController {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private CustomerServiceImpl customerService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private CompanyRepository companyRepo;

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

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
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
	public String addingProcessCustomer(@ModelAttribute Customer customer, Model model, HttpSession session, HttpServletRequest request) throws URISyntaxException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user1 = userRepo.findByUsername(auth.getName());

		String user = auth.getName();

		User addedByUser = userRepo.findByUsername(user);

		customer.setDueAmount("0");
		customer.setUser(user1);
		customer.setStatus("Active");
		customerRepo.save(customer);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";

		if (user1.getImageUrl() != null && !user1.getImageUrl().isEmpty()) {
			String image = user1.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		session.setAttribute("message", "Customer Added Successfully");

		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");

		return "redirect:" + endpoint;
	}

	@GetMapping("/customer/list")
	public String getAllCustomers(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Pageable pageable = PageRequest.of(page, size);
		Page<Customer> customers = customerRepo.showAllCustomerBYActive(userId, pageable);
		model.addAttribute("customers", customers);
		model.addAttribute("currentPage", page);

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);
		model.addAttribute("imagePath", imgpath);

		return "admin/customer_list";
	}

	@GetMapping("/customer/update/{id}")
	public String updateCustomerDetails(@PathVariable("id") Integer customerId, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		Customer customerGet = customerService.getCustomerById(customerId);
		model.addAttribute("customer", customerGet);
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
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
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		customerRepo.save(customerGet);

		return "redirect:/a2zbilling/admin/customer/list";
	}

	@GetMapping("/customer/delete/{id}")
	public String deleteCustomerById(@PathVariable("id") int id) {

		Customer customerFetched = customerService.getCustomerById(id);

		customerFetched.setStatus("inActive");
		customerRepo.save(customerFetched);

		return "redirect:/a2zbilling/admin/customer/list";
	}
	
	@PostMapping("/customer/setPaymentReminderOrUpdateDue")
	public String setPaymentReminderOrUpdateDue(@RequestParam("id") int id, @RequestParam("paymentReminderDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String paymentReminderDate, @RequestParam("duePaid") double duePaid)
	{
		Customer customer = customerRepo.findById(id).get();
		
		customer.setPaymentReminderDate(paymentReminderDate);
		
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if(paymentReminderDate != null) {
			double totalDue = Double.parseDouble(customer.getDueAmount()) - duePaid;
			customer.setDueAmount(decimalFormat.format(totalDue));
        }
		
		customerRepo.save(customer);
		return "redirect:/a2zbilling/admin/customer/list";
	}
}
