package com.billing.controller;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.List;

import org.hibernate.dialect.SimpleDatabaseVersion;
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
import com.billing.model.Parties;
import com.billing.model.PartiesTransaction;
import com.billing.model.User;
import com.billing.repositories.BrandRepository;
import com.billing.repositories.CategoryRepository;
import com.billing.repositories.ColorRepository;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.GSTRepository;
import com.billing.repositories.PartiesRepository;
import com.billing.repositories.PartiesTransectionRepository;
import com.billing.repositories.ProductRepository;
import com.billing.repositories.SizeRepository;
import com.billing.repositories.SupplierRepository;
import com.billing.repositories.UnitRepository;
import com.billing.repositories.UserRepository;
import com.billing.services.CustomerServiceImpl;
import com.billing.services.ProductServiceImpl;
import com.billing.services.SupplierServiceImpl;
import com.billing.utils.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
		
		@Autowired
		private PartiesRepository partiesRepo;
		
		@Autowired
		private PartiesTransectionRepository partiesTransectionRepo;

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
		
		// changes By Mahesh
		@PostMapping("/parties/add")
		public String partiesAddingProcess(@ModelAttribute Parties parties, HttpSession session, HttpServletRequest request)
				throws URISyntaxException {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userRepo.findByUsername(auth.getName());
			
			parties.setStatus("Active");
			
			if(parties.getOpeningBalance() == null || parties.getOpeningBalance().isEmpty()) {
				parties.setOpeningBalance("0");
			}
			user.getParties().add(parties);
			parties.setUser(user);
			partiesRepo.save(parties);
			userRepo.save(user);
			
			String referer = request.getHeader("referer");
			java.net.URI uri = new java.net.URI(referer);
			String path = uri.getPath();
			String query = uri.getQuery();
			String endpoint = path + (query != null ? "?" + query : "");
			return "redirect:" + endpoint;

		}

		// Created by Mahesh
		@GetMapping("/parties/update/{id}")
		public String updateParties(@PathVariable("id") int id, Model model) {

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

			Parties partie = partiesRepo.findById(id).get();
			model.addAttribute("partie", partie);

			return "admin/update_parties";
		}
		
		// changes By Mahesh
		@PostMapping("/parties/update")
		public String partiesUpdateProcess(@ModelAttribute Parties parties, HttpSession session) {
			Parties partie = partiesRepo.findById(parties.getId()).get();

			partie.setName(parties.getName());
			partie.setEmail(parties.getEmail());
			partie.setMobile(parties.getMobile());
			partie.setBillingAddress(parties.getBillingAddress());
			partie.setShippingAddress(parties.getShippingAddress());
			partie.setState(parties.getState());
			partie.setDate(parties.getDate());

			if (!parties.getGstType().isEmpty()) {
				partie.setGstType(parties.getGstType());
			}
			partie.setGstinNumber(parties.getGstinNumber());
			partie.setAdharNumber(parties.getAdharNumber());
			partie.setPanNumber(parties.getPanNumber());
			partie.setDrivingLicenceNumber(parties.getDrivingLicenceNumber());
			partie.setPartyGroup(parties.getPartyGroup());
			partie.setPayment(parties.getPayment());
			
			if(parties.getOpeningBalance() == null || parties.getOpeningBalance().isEmpty()) {
				partie.setOpeningBalance("0");
			}
			else partie.setOpeningBalance(parties.getOpeningBalance());

			partiesRepo.save(partie);

			return "redirect:/a2zbilling/admin/parties/list";
		}
		
		@GetMapping("/parties/list")
		public String listOfParties(Model model) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userRepo.findByUsername(auth.getName());
			int userId = user.getId();
			String username = auth.getName();
			String email = user.getEmail();
			model.addAttribute("username", username);
			model.addAttribute("email", email);
			
			Company company = companyRepo.getCompanyByUserId(user.getId());
			String companyName = company.getName();
			model.addAttribute("companyName", companyName);

			List<Parties> parties = partiesRepo.showAllActiveParties(userId);
			model.addAttribute("parties", parties);
			
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
		
		@GetMapping("/parties/transactions/list/{id}")
		public String listOfPartiesTransactions(@PathVariable("id") int id, Model model) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userRepo.findByUsername(auth.getName());
			
			String username = auth.getName();
			String email = user.getEmail();
			model.addAttribute("username", username);
			model.addAttribute("email", email);
			
			Parties parties = partiesRepo.findById(id).get();
			List<PartiesTransaction> partiesTransactions = parties.getTransactions();
			model.addAttribute("partiesTransactions", partiesTransactions);
			
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
