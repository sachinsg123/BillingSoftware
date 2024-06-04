package com.billing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.billing.model.Company;
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

@Controller
@RequestMapping("/a2zbilling/admin")
public class PartiesController {
	
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
		
		// Change by Younus
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

		@GetMapping("/parties/add")
		public String addParties(Model model) {

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

			return "admin/add_parties";
		}

		// Created by Mahesh
		@GetMapping("/parties/update")
		public String updateParties(Model model) {

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

			return "admin/update_parties";
		}
		
		@GetMapping("/parties/list")
		public String listOfParties(Model model) {

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

			return "admin/parties_list";
		}
		@GetMapping("/parties/transactions/list")
		public String listOfPartiesTransactions(Model model) {

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

			return "admin/transactions_list";
		}

}
