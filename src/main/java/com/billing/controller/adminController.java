package com.billing.controller;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.billing.model.Brand;
import com.billing.model.Category;
import com.billing.model.Color;
import com.billing.model.Company;
import com.billing.model.CompanyDto;
import com.billing.model.Customer;
import com.billing.model.GSTRate;
import com.billing.model.Product;
import com.billing.model.Size;
import com.billing.model.Supplier;
import com.billing.model.Unit;
import com.billing.model.User;
import com.billing.model.UserDto;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/a2zbilling/admin")
public class adminController {

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

	// Created by Mahesh
	@GetMapping("/viewAdminProfile")
	public String viewAdminProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

	    
	    model.addAttribute("user", user);
	    
	    Company company = companyRepo.getCompanyByUserId(user.getId());
	    String companyName = company.getName();
	    model.addAttribute("companyName", companyName);
	    
	    String adminImg = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
	    
	    if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	adminImg = "/img/userImage/" + image;
	    }
	    
	    model.addAttribute("adminImg", adminImg);

		model.addAttribute("user", user);

		model.addAttribute("companyName", companyName);

		
		model.addAttribute("imagePath", adminImg);

		// Changes by Younus - code to render business logo
		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		// Changes by Younus -code to render Signature
		String sign = company.getSignature();
		String companySign = "/img/companysignature/" + sign;
		model.addAttribute("companySign", companySign);

		return "/admin/view_Admin_Profile";
	}

	@GetMapping("/")
	public String home(Model model, HttpSession session) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getPrincipal());
		System.out.println(auth.getName());

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());
//		String companyName = company.getName();

		// Code to render Admin logo
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    } 
		
		model.addAttribute("imagePath", imgpath);

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		
		String companyName = company.getName();

		model.addAttribute("company", company);
		model.addAttribute("companyName", companyName);

		// Changes by Younus - code to render business logo
		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		// Changes by Younus -code to render Signature
		String sign = company.getSignature();
		String companySign = "/img/companysignature/" + sign;
		model.addAttribute("companySign", companySign);

		// Changes by Younus -(For Customer Count- Dynamically)
		long customercount = customerService.getCustomerCount();
		model.addAttribute("customercount", customercount);

		// Changes by Younus -(For Supplier Count- Dynamically)
		long suppliercount = supplierService.getSupplierCount();
		model.addAttribute("suppliercount", suppliercount);

		return "home";

	}

	@GetMapping("/product/add")
	public String addProductByAdmin(Model model) {
		
		List<Supplier> suppliers=supplierRepo.findAll();
		model.addAttribute("suppliers", suppliers);

		List<Customer> customerList = customerRepo.findAll();
		model.addAttribute("customers", customerList);

		List<Category> categoryList = categoryRepo.findAll();
		model.addAttribute("categories", categoryList);

		List<Color> colors = colorRepo.findAll();
		model.addAttribute("colors", colors);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		model.addAttribute("companyName", companyName);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/add_product_form";

	}

	@PostMapping("/product/add")
	public String processProductAdding(@ModelAttribute Product product,
			@RequestParam("imageFile") MultipartFile imageFile, @RequestParam("productSize") String productSizeValue,
			@RequestParam("productColor") String colorName, @RequestParam("productBrand") String brandName,
			HttpSession session) throws IOException {

		if (imageFile.getOriginalFilename().isEmpty()) {

			// imageFile.getOriginalFilename();
			product.setImageUrl("default.png");
		} else {
			// String uploadDir = "./static/productImages";
			String fileName = imageFile.getOriginalFilename();
			// Path filePath = Paths.get(StringUtils.ImagePaths.productImageUrl).getFile();
			File filePath = new ClassPathResource("/static/img/products/").getFile();
			Path path = Paths.get(filePath.getAbsolutePath() + File.separator + fileName);
			Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			System.out.println(fileName);
			System.out.println(path);
			System.out.println(filePath);
			product.setImageUrl(fileName);
		}

		// adding size
		Size sizeOccured = sizeRepo.findBySizeValue(productSizeValue);

		if (sizeOccured != null) {

			product.setSize(sizeOccured);
		} else {

			Size s = new Size();
			s.setSizeValue(productSizeValue);
			sizeRepo.save(s);
			product.setSize(s);
		}

		// adding color
		Color colorFound = colorRepo.findByName(colorName);
		Color noColorChoosed = colorRepo.findByName("No color choosed");

		if (colorFound != null) {

			product.setColor(colorFound);
		} else if (colorName.equals("No choosed color")) {

			product.setColor(noColorChoosed);

		} else {
			Color co = new Color();
			co.setName(colorName);
			colorRepo.save(co);
			product.setColor(co);
		}

		// add brand into product
		Brand brandFound = brandRepo.findByName(brandName);

		if (brandFound != null) {

			product.setBrand(brandFound);
		} else {

			Brand b = new Brand();
			b.setName(brandName);
			b.setLogo("default.png");
			brandRepo.save(b);
			product.setBrand(b);
		}

		Category cat = categoryRepo.findByCategoryName(product.getCategory().getCategoryName());

		if (cat == null) {

			product.setCategory(null);
		}

		product.setCategory(cat);
		product.setStatus("Active");

		productRepo.save(product);

		System.out.println(product);
		session.setAttribute("message", "Product Added Successfully");

		return "redirect:/a2zbilling/admin/product/add";

	}

	// showing all products
	@GetMapping("/product/list")
	public String showAllProduct(Model model) {

		List<Product> allProducts = productService.getAllProducts();
		model.addAttribute("products", allProducts);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		model.addAttribute("companyName", companyName);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/product_list";
	}

	// update form handler
	@GetMapping("/product/edit/{id}")
	public String productEditForm(@PathVariable("id") String id, Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);
		Optional<Product> Founded = productRepo.findById(Integer.parseInt(id));
		Product product = Founded.get();
		model.addAttribute("product", product);
		List<Category> categories = categoryRepo.findAll();
		model.addAttribute("categories", categories);
		List<Color> colors = colorRepo.findAll();
		model.addAttribute("colors", colors);

<<<<<<< HEAD
		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

=======
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		return "admin/edit_product";

	}

	@PostMapping("/product/edit")
	public String productUpdateProcess(@ModelAttribute Product product,
			@RequestParam("imageFile") MultipartFile imageFile, @RequestParam("productSize") String productSizeValue,
			@RequestParam("productColor") String colorName, @RequestParam("productBrand") String brandName)
			throws IOException {

		System.out.println("data getting " + product);
		Optional<Product> found = productRepo.findById(product.getId());
		Product productFound = found.get();
		productFound.setId(product.getId());
		productFound.setName(product.getName());
		productFound.setAddedDate(product.getAddedDate());
		productFound.setImageUrl(product.getImageUrl());
		if (imageFile.getOriginalFilename().isEmpty()) {

			// imageFile.getOriginalFilename();
			product.setImageUrl("default.png");
		} else {
//			   String uploadDir = "./static/productImages";
			String fileName = imageFile.getOriginalFilename();
//			   Path filePath = Paths.get(StringUtils.ImagePaths.productImageUrl).getFile();
			File filePath = new ClassPathResource("/static/img/products/").getFile();
			Path path = Paths.get(filePath.getAbsolutePath() + File.separator + fileName);
			Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			System.out.println(fileName);
			System.out.println(path);
			System.out.println(filePath);
			product.setImageUrl(fileName);
		}
		Size sizeOccured = sizeRepo.findBySizeValue(productSizeValue);

		if (sizeOccured != null) {

			product.setSize(sizeOccured);
		} else {

			Size s = new Size();
			s.setSizeValue(productSizeValue);
//			sizeRepo.save(s);
			product.setSize(s);
		}

		// adding color
		Color colorFound = colorRepo.findByName(colorName);
		Color noColorChoosed = colorRepo.findByName("No color choosed");

		if (colorFound != null) {

			product.setColor(colorFound);
		} else if (colorName.equals("No choosed color")) {

			product.setColor(noColorChoosed);

		} else {
			Color co = new Color();
			co.setName(colorName);
//			colorRepo.save(co);
			product.setColor(co);
		}

		// add brand into product
		Brand brandFound = brandRepo.findByName(brandName);

		if (brandFound != null) {

			product.setBrand(brandFound);
		} else {

			Brand b = new Brand();
			b.setName(brandName);
			b.setLogo("default.png");
//			brandRepo.save(b);
			product.setBrand(b);
		}

		Category cat = categoryRepo.findByCategoryName(product.getCategory().getCategoryName());

		if (cat == null) {

			product.setCategory(null);
		}

		product.setCategory(cat);
		product.setStatus("Active");

		System.out.println(product);

//		productRepo.save(product);		

		return "redirect:/a2zbilling/admin/product/list";
	}

	// Created by Mahesh
	@GetMapping("/parties/add")
	public String addParties(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
		
		return "admin/add_parties";
	}
	
	// Created by Mahesh
	@GetMapping("/parties/update")
	public String updateParties(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
		
		return "admin/update_parties";
	}
	
	// Created by Mahesh
	@GetMapping("/parties/list")
	public String listOfParties(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
		
		return "admin/parties_list";
	
	}
	
	//change by Mahesh
	@GetMapping("/parties/delete")
	public String deleteParties() {

		return "redirect:/a2zbilling/admin/parties/list";

	}
	
	// Created by Mahesh
	@GetMapping("/parties/transactions/list")
	public String listOfPartiesTransactions(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
		
		return "admin/transactions_list";
	}
	
	// Created by Mahesh
	@GetMapping("/parties/transactions/update")
	public String updateTransactions(Model model)
	{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
		
		return "admin/update_transactions";
	}
	
	// Created by Mahesh
	@GetMapping("/parties/transactions/delete")
	public String deleteTransaction() {

		return "redirect:/a2zbilling/admin/parties/transactions/list";
	}
	
	@GetMapping("/customer/add")
	public String customerAddForm(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);
		
		//Code to Render admin on our page
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "/admin/add_customer_form";

	}
<<<<<<< HEAD

	// Created by Mahesh
	@GetMapping("/parties/add")
	public String addParties(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
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
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/update_parties";
	}

	// Created by Mahesh
	@GetMapping("/parties/list")
	public String listOfParties(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/parties_list";
	}

	// Created by Mahesh
	@GetMapping("/parties/transactions/list")
	public String listOfPartiesTransactions(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/transactions_list";
	}

=======
	
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
	@PostMapping("/customer/add")
	public String addingProcessCustomer(@ModelAttribute Customer customer,Model model, HttpSession session) {

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
		if(user1.getImageUrl() != null)
	    {
	    	String image = user1.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
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

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
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

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "/admin/update_customer";

	}

	@PostMapping("/customer/update")
	public String customerUpdateProcessing(@ModelAttribute Customer customer,Model model,HttpSession session) {

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
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
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

	// Changes By Mahesh
	@GetMapping("/supplier/add")
	public String addSupplierForm(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
<<<<<<< HEAD

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

=======
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		return "admin/add_supplier";

	}

	// changes By Mahesh
	@PostMapping("/supplier/add")
	public String supplierAddingProcess(@ModelAttribute Supplier supplier, HttpSession session) {

		if (supplier.getAddedDate().isEmpty()) {

			Date d = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String formattedDate = formatter.format(d);
			supplier.setAddedDate(formattedDate);
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			String admin = auth.getName();
			User user = userRepo.findByUsername(admin);
			supplier.setUser(user);
		}

		supplier.setStatus("Active");

		supplierRepo.save(supplier);

		session.setAttribute("message", "supplier added Successfully");

		return "redirect:/a2zbilling/admin/supplier/add";
	}

	// change by Mahesh
	@GetMapping("/supplier/list")
	public String listOfSuppliers(Model model) {

		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier();

		model.addAttribute("suppliers", suppliers);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/supplier_list";

	}

	// change by Mahesh
	@GetMapping("/supplier/update/{id}")
	public String updateSupplier(@PathVariable("id") int id, Model model) {

		Optional<Supplier> supplierGet = supplierRepo.findById(id);
		Supplier supplier = supplierGet.get();
		model.addAttribute("supplier", supplier);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/update_supplier";

	}

	// update supplier processing
	// change by Mahesh
	@PostMapping({ "/supplier/update", "/supplier/update/" })
	public String supplierUpdateProcess(@ModelAttribute Supplier supplier, HttpSession session) {

		Optional<Supplier> supplierGet = supplierRepo.findById(supplier.getId());
		Supplier supp = supplierGet.get();
		supp.setName(supplier.getName());
		supp.setEmail(supplier.getEmail());
		if (!supplier.getAddedDate().isEmpty()){
			
			supp.setAddedDate(supplier.getAddedDate());
		}
		supp.setAddress(supplier.getAddress());
		supp.setMobile(supplier.getMobile());

		supplierRepo.save(supp);

		session.setAttribute("message", "supplier Updated Successfully --!");

		return "redirect:/a2zbilling/admin/supplier/list";
	}

	// change by Mahesh
	@GetMapping("/supplier/delete/{id}")
	public String deleteSupplier(@PathVariable("id") int id) {

		Optional<Supplier> supplierGet = supplierRepo.findById(id);
		Supplier supplier = supplierGet.get();

		supplier.setStatus("InActive");

		supplierRepo.save(supplier);

		return "redirect:/a2zbilling/admin/supplier/list";

	}

	// edit firm form
	@GetMapping("/edit/firm")
	public String editFirmForm(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getPrincipal());
		System.out.println(auth.getName());
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		model.addAttribute("username", username);
		Company company = companyRepo.getCompanyByUserId(user.getId());
		model.addAttribute("company", company);
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/edit_firm";

	}

	// Changes by Younus - Edit & store user data dynamically into db

	@PostMapping("/edit/firm")
	public String editFirm(@ModelAttribute CompanyDto companyDto, Model model) {

		// Retrieve the currently logged-in user data
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String admin = auth.getName();
		User user = userRepo.findByUsername(admin);

		// Retrieve the company being edited
		Optional<Company> currentCompanyOptional = companyRepo.findById(companyDto.getId());
		if (!currentCompanyOptional.isPresent()) {
			// Handle the case where the company is not found
			return "redirect:/a2zbilling/admin/"; // Or show an error message
		}
		Company currentCompany = currentCompanyOptional.get();

		// Update the company details with the submitted form data
		currentCompany.setUser(user);
		currentCompany.setName(companyDto.getName());
		currentCompany.setAddress(companyDto.getAddress());
		currentCompany.setEmail(companyDto.getEmail());
		currentCompany.setPhone(companyDto.getPhone());
		currentCompany.setDescription(companyDto.getDescription());

		// Handle logo upload
		if (companyDto.getLogo() != null && !companyDto.getLogo().isEmpty()) {
			try {
				MultipartFile logoFile = companyDto.getLogo();
				String fileName = uploadFile(logoFile, "companylogo");
				currentCompany.setLogo(fileName);
			} catch (IOException ex) {
				// Handle file upload error
				ex.printStackTrace();
				model.addAttribute("error", "Failed to upload logo: " + ex.getMessage());
			}
		}

		// Handle Signature Upload
		if (companyDto.getSignature() != null && !companyDto.getSignature().isEmpty()) {
			try {
				MultipartFile signFile = companyDto.getSignature();
				String signFileName = uploadFile(signFile, "companysignature");
				currentCompany.setSignature(signFileName);
			} catch (IOException ex) {
				// Handle file upload error
				ex.printStackTrace();
				model.addAttribute("error", "Failed to upload Signature: " + ex.getMessage());
			}
		}

		// Save the updated company
		companyRepo.save(currentCompany);

		return "redirect:/a2zbilling/admin/";
	}

	private String uploadFile(MultipartFile file, String directory) throws IOException {
		Date date = new Date();
		String storageFileName = date.getTime() + "_" + file.getOriginalFilename();

		String uploadDir = "src/main/resources/static/img/" + directory + "/";
		Path uploadPath = Paths.get(uploadDir);

		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
		}

		return storageFileName;
	}
	/*
	 * edit firm Changes -> I've corrected the typo in companyDto.getEmail() instead
	 * of companyDto.getAddress() for setting the company's email. I've extracted
	 * the logo upload logic into a separate method uploadLogo for better code
	 * organization and reuse. The uploadLogo method handles the file upload process
	 * and returns the filename of the uploaded logo. If an error occurs during the
	 * upload process, it throws an IOException. In the editFirm method, I've added
	 * error handling for file upload failures. If an error occurs during logo
	 * upload, it prints the stack trace and adds an error message to the model.
	 */

	@PostMapping("/unit/add")
	public String addUnit(@ModelAttribute Unit unit) {

		Unit unitFound = unitRepo.findByunitName(unit.getUnitName());

		if (unitFound == null) {

			unit.setUnitName(unit.getUnitName());
			unit.setUnitCode(unit.getUnitCode());
			unitRepo.save(unit);

		} else {

			unitFound.setUnitCode(unit.getUnitCode());
			unitFound.setUnitName(unit.getUnitName());
			unitRepo.save(unitFound);

		}

		return "redirect:/a2zbilling/admin/";
	}

	// adding gst by admin
	@PostMapping("/gst/add")
	public String gstAddingByAdmin(@ModelAttribute GSTRate gst) {

		GSTRate gstFound = gstRepo.findBygstRate(gst.getGstRate());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String admin = auth.getName();
		User user = userRepo.findByUsername(admin);

		if (gstFound != null) {

			gstFound.setGstRate(gst.getGstRate());
			gstFound.setCgstRate(gst.getCgstRate());
			gstFound.setSgstRate(gst.getSgstRate());
			gstFound.setUser(user);
			System.out.println(gstFound);
			gstRepo.save(gstFound);

		} else {

			gst.setGstRate(gst.getGstRate());
			gst.setCgstRate(gst.getCgstRate());
			gst.setSgstRate(gst.getSgstRate());
			gst.setUser(user);
			System.out.println(gst);
			gstRepo.save(gst);

		}

		System.out.println(gstFound);

		return "redirect:/a2zbilling/admin/";
	}

	@PostMapping("/category/add")
	public String addingCategory(@ModelAttribute Category category, HttpSession session) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String admin = auth.getName();
		User user = userRepo.findByUsername(admin);

		Date d = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = formatter.format(d);
		category.setAddedDate(formattedDate);
		Category cFound = categoryRepo.findByCategoryName(category.getCategoryName());

		if (cFound == null) {

			category.setStatus("Active");
			category.setUser(user);

			categoryRepo.save(category);

		} else if (category.getCategoryName().equals(cFound.getCategoryName())) {

			session.setAttribute("message", "Category Already added !");

		}

		return "redirect:/a2zbilling/admin/";
	}

	@GetMapping("/category/list")
	public String listOfCategories(Model model) {

		List<Category> categories = categoryRepo.findByActiveCategory();
		model.addAttribute("categories", categories);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		model.addAttribute("companyName", companyName);
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/categories_list";

	}

	// Edit category form
	@GetMapping("/category/update/{id}")
	public String updateCategory(@PathVariable("id") String id, Model model) {

		Optional<Category> category = categoryRepo.findById(Integer.parseInt(id));
		Category catFound = category.get();
		model.addAttribute("category", catFound);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		model.addAttribute("companyName", companyName);

		return "admin/update_category";
	}

	@PostMapping("/category/update")
	public String categoryUpdateProcess(@ModelAttribute Category category) {

		Optional<Category> cFound = categoryRepo.findById(category.getId());
		Category categoryF = cFound.get();
		categoryF.setId(category.getId());
		categoryF.setCategoryName(category.getCategoryName());
		Date d = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		String formattedDate = formatter.format(d);
		categoryF.setAddedDate(formattedDate);
		categoryF.setStatus("Active");
		categoryRepo.save(categoryF);

		return "redirect:/a2zbilling/admin/category/list";

	}

	// clear session
	@GetMapping("/clearSessionAttribute")
	public String clearSession(HttpSession session, HttpServletRequest request) {
		String referer = request.getHeader("referer");
		if (session.getAttribute("message") != null) {

			session.removeAttribute("message");
			if (referer != null && !referer.isEmpty()) {
				return "redirect:" + referer;
			}
			return "redirect:/a2zbilling/admin/";
		}
		session.removeAttribute("message");

		return "redirect:/a2zbilling/admin/customer/add";
	}

	// Created by Younus - Get Purchase bill list
	@GetMapping("/purchasebill/list")
	public String purchaseBillList(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
<<<<<<< HEAD
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasebill_list";
	}

	// Created by Younus - add Purchase bill
	@GetMapping("/purchasebill/add")
	public String addPurchaseBill(Model model) {

<<<<<<< HEAD
		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.findAll();
		model.addAttribute("units", units);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.findAll();
		model.addAttribute("sizes", sizes);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
<<<<<<< HEAD

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

=======
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		return "admin/purchasebill_add";

	}

	// Created by Younus - Update PurchaseBill form
	@GetMapping("/purchasebill/update")
	public String updatePurchaseBill(Model model) {
<<<<<<< HEAD

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasebill_update";

	}
	

	
	

	// Created by Mahesh - get sale list
	@GetMapping("/sales/list")
	public String salesList(Model model) {
<<<<<<< HEAD

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		List<Customer> customers = customerRepo.findAll();
		model.addAttribute("customers",customers);
		
		List<Product> products = productRepo.findAll();
		model.addAttribute("products",products);
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
<<<<<<< HEAD

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);
=======
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		return "admin/sales_list";

	}

	// Created by Younus - add sales
	@GetMapping("/sales/add")
	public String addSales(Model model) {
<<<<<<< HEAD

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);
<<<<<<< HEAD

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

=======
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		return "admin/sales_add";

	}

	// Created by Younus - Update PurchaseBill form
	@GetMapping("/sales/update")
	public String updatesales(Model model) {
<<<<<<< HEAD

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/sales_update";

	}

	// Created by Younus - add Item
	@GetMapping("/Item/add")
	public String addItem(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
<<<<<<< HEAD
		Company company = companyRepo.getCompanyByUserId(user.getId());

=======
		
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if(user.getImageUrl() != null)
	    {
	    	String image = user.getImageUrl();
	    	imgpath = "/img/userImage/" + image;
	    }
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/Item_add";

	}

<<<<<<< HEAD
	// Created by Younus - to Manage Stock
=======
	
	
	
	//Created by Younus - to Manage Stock
>>>>>>> 2c020f4b28829da39a5d8a7b53d95405ae30300b
	@GetMapping("/managestock")
	public String manageStock(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/manage_stock";

	}

	// created by Mahesh
	@GetMapping("/generetOTP/{mobile}")
	public ResponseEntity<String> generetOTP(@PathVariable("mobile") String mobileNumber) {
		
		User user = userRepo.findByMobile(mobileNumber); // Implement this method in your UserRepository
		
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid mobile number");
        }
  
		return ResponseEntity.ok("admin/sales_list");


	}
}
