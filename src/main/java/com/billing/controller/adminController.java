package com.billing.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.billing.model.Brand;
import com.billing.model.BrandDto;
import com.billing.model.Category;
import com.billing.model.Charges;
import com.billing.model.Company;
import com.billing.model.CompanyDto;
import com.billing.model.Customer;
import com.billing.model.Expense;
import com.billing.model.GSTRate;
import com.billing.model.Parties;
import com.billing.model.PartiesTransaction;
import com.billing.model.Product;
import com.billing.model.PurchaseOrder;
import com.billing.model.Sales;
import com.billing.model.Size;
import com.billing.model.Stock;
import com.billing.model.Supplier;
import com.billing.model.Unit;
import com.billing.model.User;
import com.billing.model.UserDto;
import com.billing.repositories.BrandRepository;
import com.billing.repositories.CategoryRepository;
import com.billing.repositories.ChargesRepository;
import com.billing.repositories.ColorRepository;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.ExpenseRepository;
import com.billing.repositories.GSTRepository;
import com.billing.repositories.PartiesRepository;
import com.billing.repositories.PartiesTransectionRepository;
import com.billing.repositories.ProductRepository;
import com.billing.repositories.PurchaseOrderRepository;
import com.billing.repositories.SalesRepository;
import com.billing.repositories.SizeRepository;
import com.billing.repositories.StockRepository;
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
	private ChargesRepository chargesRepo;

	@Autowired
	private BrandRepository brandRepo;

	@Autowired
	private PartiesRepository partiesRepo;

	@Autowired
	private PartiesTransectionRepository partiesTransectionRepo;

	@Autowired
	private SalesRepository salesRepo;

	@Autowired
	private StockRepository stockRepo;

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepo;

	@Autowired
	private ExpenseRepository expenseRepo;

	@GetMapping("/viewAdminProfile")
	public String viewAdminProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		model.addAttribute("user", user);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String adminImg = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";

		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			adminImg = StringUtils.ImagePaths.userImageUrl + image;
		}

		model.addAttribute("user", user);
		model.addAttribute("companyName", companyName);
		model.addAttribute("imagePath", adminImg);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		String sign = company.getSignature();
		String companySign = "/img/companysignature/" + sign;
		model.addAttribute("companySign", companySign);

		return "/admin/view_Admin_Profile";
	}

	@GetMapping("/updateAdminProfile")
	public String updateAdminProfile(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		model.addAttribute("user", user);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String adminImg = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			adminImg = StringUtils.ImagePaths.userImageUrl + image;
		}

		model.addAttribute("user", user);
		model.addAttribute("companyName", companyName);
		model.addAttribute("imagePath", adminImg);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		String sign = company.getSignature();
		String companySign = "/img/companysignature/" + sign;
		model.addAttribute("companySign", companySign);

		return "/admin/update_Admin_Profile";

	}

	@PostMapping("/updateAdminProfile")
	public String updateProcessUser(@ModelAttribute UserDto userDto, HttpSession session) {
		System.out.println("This is a userId "+userDto.getId());
		
		User user = userRepo.findById(userDto.getId()).get();
		MultipartFile image = userDto.getImageUrl();
		if (!image.isEmpty()) {
			Date date = new Date();
			String storageFileName = date.getTime() + "_" + image.getOriginalFilename();

			try {
				String uploadDir = "src/main/resources/static/img/userImage/";
				Path uploadPath = Paths.get(uploadDir);

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (Exception ex) {
				System.out.println("Exception: " + ex.getMessage());
			}
			user.setImageUrl(storageFileName);
		}
		String userName = userDto.getUsername();
		user.setUsername(userName);
		User user1 = userRepo.findByEmail(userDto.getEmail());
		if(user1 != null && user.getId() != user1.getId())
        {
       	 session.setAttribute("message", "This Email Already Used");
       	 return "redirect:/a2zbilling/admin/viewAdminProfile";
        }
        user1 = userRepo.findByMobile(userDto.getMobile());
        if(user1 != null &&user.getId() != user1.getId())
        {
       	 session.setAttribute("message", "This Mobile Number Already Used");
       	 return "redirect:/a2zbilling/admin/viewAdminProfile";
        }
        user.setEmail(userDto.getEmail());
        user.setMobile(userDto.getMobile());
		Company company = user.getCompany();

		if (!userDto.getCompanyname().isEmpty()) {
			company.setName(userDto.getCompanyname());
		}
		company.setUser(user);

		userRepo.save(user);
		companyRepo.save(company);

		session.setAttribute("message", "User Updated Successfully");

		return "redirect:/a2zbilling/admin/viewAdminProfile";
	}

	// Home Controller
	@GetMapping("/")
	public String home(Model model, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Company company = companyRepo.getCompanyByUserId(userId);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";

		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		String companyName = company.getName();
		model.addAttribute("company", company);
		model.addAttribute("companyName", companyName);

		String imgpath1 = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath1 = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath1);

		// for Alert if product quantity is less then min quantity
		List<Product> products = productRepo.showAllActiveProduct(userId);
		List<Product> minStockProducts = new ArrayList<Product>();

		for (Product product : products) {
			Stock stock = product.getStock();

			if (stock != null && Integer.parseInt(stock.getQuantity()) <= Integer.parseInt(stock.getMinQuantity())) {
				minStockProducts.add(product);
			}
		}

		StringBuilder productNamesBuilder = new StringBuilder();
		boolean isFirst = true;
		for (Product product : minStockProducts) {
			if (!isFirst) {
				productNamesBuilder.append(", ");
			}
			productNamesBuilder.append("\"").append(product.getName()).append(" :- \"")
					.append(product.getStock().getQuantity()).append("\"");
			isFirst = false;
		}
		String productNamesString = productNamesBuilder.toString();

		model.addAttribute("productNamesString", productNamesString);
		model.addAttribute("minStockProducts", minStockProducts);
        		
		List<Customer> customers = customerRepo.showAllCustomerBYActive(userId);
        LocalDate currentDate = LocalDate.now();

        List<Customer> alertCustomers = new ArrayList<>();

        for (Customer customer : customers) {
            String customerDate = customer.getPaymentReminderDate();

            // Check if customerDate is null or empty
            if (customerDate == null || customerDate.isEmpty() || Double.parseDouble(customer.getDueAmount()) <= 0) {
                // Log the error or handle the null/empty case as needed
                System.out.println("Customer date is null or empty for customer: " + customer.getName());
                continue; // Skip this iteration
            }

            try {
                LocalDate customerProvidedDate = LocalDate.parse(customerDate);

                if (currentDate.isEqual(customerProvidedDate) || currentDate.isAfter(customerProvidedDate)) {
                    alertCustomers.add(customer);
                }
            } catch (DateTimeParseException e) {
                // Log the error or handle the invalid date format case as needed
                System.out.println("Invalid date format for customer: " + customer.getName() + ", date: " + customerDate);
            }
        }

        // Add the list of customers needing alert to the model
        model.addAttribute("alertCustomers", alertCustomers);
        
		List<Sales> sales = salesRepo.showAllActiveSales(userId);
		int salesRecordCount = sales.size();
		model.addAttribute("salesRecordCount", salesRecordCount);
		model.addAttribute("sales", sales);

		List<PartiesTransaction> partiesTransections = partiesTransectionRepo.showAllActivePartiesTransection(userId);
		int purchaseRecordCount = partiesTransections.size();
		model.addAttribute("purchaseRecordCount", purchaseRecordCount);
		model.addAttribute("partiesTransections", partiesTransections);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		String sign = company.getSignature();
		String companySign = "/img/companysignature/" + sign;
		model.addAttribute("companySign", companySign);

		long customercount = customerService.getCustomerCount();
		model.addAttribute("customercount", customercount);

		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		long suppliercount = parties.size();
		model.addAttribute("suppliercount", suppliercount);
		System.out.println("suppliercount            ");
		return "home";
	}

	@GetMapping("/supplier/add")
	public String supplierAddForm(Model model) {
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
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}

		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "/admin/add_supplier";

	}

	@PostMapping("/supplier/add")
	public String supplierAddingProcess(@ModelAttribute Supplier supplier, HttpSession session,
			HttpServletRequest request) throws URISyntaxException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (supplier.getAddedDate().isEmpty()) {
			Date d = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String formattedDate = formatter.format(d);
			supplier.setAddedDate(formattedDate);
		}

		if (auth != null) {
			String admin = auth.getName();
			User user = userRepo.findByUsername(admin);
			supplier.setUser(user);
		}

		supplier.setStatus("Active");
		supplierRepo.save(supplier);

		session.setAttribute("message", "Supplier added Successfully");
		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");

		return "redirect:" + endpoint;
	}

	@GetMapping("/supplier/list")
	public String listOfSuppliers(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		int id = user.getId();
		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier(id);
		model.addAttribute("suppliers", suppliers);

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

		return "admin/supplier_list";
	}

	@GetMapping("/supplier/update/{id}")
	public String updateSupplier(@PathVariable("id") int id, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Optional<Supplier> supplierGet = supplierRepo.findById(id);
		Supplier supplier = supplierGet.get();
		model.addAttribute("supplier", supplier);

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
		return "admin/update_supplier";
	}

	@PostMapping({ "/supplier/update", "/supplier/update/" })
	public String supplierUpdateProcess(@ModelAttribute Supplier supplier, HttpSession session) {
		Optional<Supplier> supplierGet = supplierRepo.findById(supplier.getId());
		Supplier supp = supplierGet.get();
		supp.setName(supplier.getName());
		supp.setEmail(supplier.getEmail());

		if (!supplier.getAddedDate().isEmpty()) {
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
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		model.addAttribute("companyName", company.getName());
		model.addAttribute("company", company);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}

		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/edit_firm";

	}

	@PostMapping("/edit/firm")
	public String editFirm(@ModelAttribute CompanyDto companyDto, RedirectAttributes redirectAttributes) {
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
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload logo: " + ex.getMessage());
			}
		}

		if (companyDto.getSignature() != null && !companyDto.getSignature().isEmpty()) {
			try {
				MultipartFile signFile = companyDto.getSignature();
				String signFileName = uploadFile(signFile, "companysignature");
				currentCompany.setSignature(signFileName);
			} catch (IOException ex) {
				// Handle file upload error
				ex.printStackTrace();
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload Signature: " + ex.getMessage());
			}
		}

		// Save the updated company
		companyRepo.save(currentCompany);

		// Add success message to redirect attributes
		redirectAttributes.addFlashAttribute("successMessage", "Firm updated successfully");

		return "redirect:/a2zbilling/admin/edit/firm";
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

	@PostMapping("/unit/add")
	public String addUnit(@ModelAttribute Unit unit, HttpSession session, HttpServletRequest request)
			throws URISyntaxException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Unit unitFound = unitRepo.findByunitName(unit.getUnitName());

		if (unitFound == null) {

			unit.setUnitName(unit.getUnitName());
			unit.setUnitCode(unit.getUnitCode());

			unit.setUser(user);
			unitRepo.save(unit);
			user.getUnits().add(unit);
			userRepo.save(user);
			session.setAttribute("message", "Unit Added Successfully !!");

		} else {

			unitFound.setUnitCode(unit.getUnitCode());
			unitFound.setUnitName(unit.getUnitName());
			unitRepo.save(unitFound);
			session.setAttribute("message", "Unit Updated Successfully !!");
		}

		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");

		return "redirect:" + endpoint;
	}

	@PostMapping("/gst/add")
	public String gstAddingByAdmin(@ModelAttribute GSTRate gst, HttpSession session) {

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
			session.setAttribute("message", "GST Updated Successfully !!");
		} else {

			gst.setGstRate(gst.getGstRate());
			gst.setCgstRate(gst.getCgstRate());
			gst.setSgstRate(gst.getSgstRate());
			gst.setUser(user);
			System.out.println(gst);
			gstRepo.save(gst);
			session.setAttribute("message", "GST Added Successfully !!");
		}

		return "redirect:/a2zbilling/admin/";
	}

	@PostMapping("/charges/add")
	public String chargesAddingByAdmin(@ModelAttribute Charges charges, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		Charges chargesFound = chargesRepo.findByName(charges.getName());
		if (chargesFound == null) {
			user.getCharges().add(charges);
			charges.setUser(user);
			chargesRepo.save(charges);
			userRepo.save(user);
			session.setAttribute("message", "Charges Added Successfully !!");
		} else {
			chargesFound.setName(charges.getName());
			chargesFound.setPrice(charges.getPrice());

			chargesRepo.save(chargesFound);
			session.setAttribute("message", "Charges Updated Successfully !!");
		}

		return "redirect:/a2zbilling/admin/";
	}

	@PostMapping("/category/add")
	public String addingCategory(@ModelAttribute Category category, HttpSession session, HttpServletRequest request)
			throws URISyntaxException {

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
			session.setAttribute("message", "Category added successfully!");
		} else if (category.getCategoryName().equals(cFound.getCategoryName())) {
			session.setAttribute("message", "Category already exists!");
		}

		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");

		return "redirect:" + endpoint;
		// return "redirect:/a2zbilling/admin/product/add";
	}

	@GetMapping("/category/list")
	public String listOfCategories(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Pageable pageable = PageRequest.of(page, size);
		Page<Category> categories = categoryRepo.findByActiveCategory(userId, pageable);
		model.addAttribute("categories", categories);
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
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
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

	// Created by Younus -for soft delete (Status - Inactive)
	@GetMapping("/category/delete/{id}")
	public String deleteCategoryById(@PathVariable("id") int id) {

		Category category = categoryRepo.findById(id).get();
		category.setStatus("InActive");
		categoryRepo.save(category);

		return "redirect:/a2zbilling/admin/category/list";
	}

	// clear session
	@GetMapping("/clearSessionAttribute")
	public String clearSession(HttpSession session, HttpServletRequest request) throws URISyntaxException {
		String referer = request.getHeader("referer");
		if (session.getAttribute("message") != null) {

			session.removeAttribute("message");
			if (referer != null && !referer.isEmpty()) {
				return "redirect:" + referer;
			}
			return "redirect:/a2zbilling/admin/";
		}
		session.removeAttribute("message");

		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");
		return "redirect:" + endpoint;
	}

	// Created by Younus - to update transections
	@GetMapping("/purchasebill/transection")
	public String purchaseBillList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
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

		Pageable pageable = PageRequest.of(page, size);
		Page<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllActivePartiesTransection(userId,
				pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);
		model.addAttribute("currentPage", page);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasebill_transection";
	}

	@GetMapping("/purchasebill/add")
	public String addPurchaseBill(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.showAllActiveUnit(userId);
		model.addAttribute("units", units);

		// to render Size list on Purchase bill page
		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

		// To get product Name data from db
		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		// To get Parties Name data from db
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		String purchaseBillString = partiesTransectionRepo.maxPurchaseBillNo(userId);
		if (purchaseBillString != null && !purchaseBillString.isEmpty()) {
			String newPurchaseBillNo = purchaseBillString.substring(0, 5);
			int no = Integer.parseInt(purchaseBillString.substring(5, purchaseBillString.length()));
			no += 1;
			newPurchaseBillNo += no;
			model.addAttribute("newPurchaseBillNo", newPurchaseBillNo);
		} else {
			String newPurchaseBillNo = "PB - 1";
			model.addAttribute("newPurchaseBillNo", newPurchaseBillNo);
		}

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(userId);

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasebill_add";
	}

	@PostMapping("/purchasebill/add")
	public String addPurchaseBillProcess(@ModelAttribute PartiesTransaction partiesTransaction, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		String quantity = partiesTransaction.getQuantity();
		int[] quantityArray = Arrays.stream(quantity.split(",")).mapToInt(Integer::parseInt).toArray();

		String taxInPercentage = partiesTransaction.getTaxInPercentage();
		int[] taxInPercentageArray = Arrays.stream(taxInPercentage.split(",")).mapToInt(Integer::parseInt).toArray();
		user.getPartiesTransactions().add(partiesTransaction);
		partiesTransaction.setUser(user);

		// update the parties due
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
		Parties parties = partiesTransaction.getParties();
		Double amount = Double.parseDouble(parties.getOpeningBalance()) - Double.parseDouble(partiesTransaction.getDues());
		if (amount > 0) {
			parties.setPayment("toReceive");
		} else {
			parties.setPayment("toPay");
		}
		String  stringAmount = decimalFormat.format(amount);
		parties.setOpeningBalance(stringAmount);
		parties.getTransactions().add(partiesTransaction);
		partiesRepo.save(parties);

		partiesTransaction.setPurchaseType("Purchase");
		partiesTransectionRepo.save(partiesTransaction);
		userRepo.save(user);
		partiesTransaction.setStatus("Active");

		// update the products quantity
		List<Product> products = partiesTransaction.getProducts();
		List<String> prices = partiesTransaction.getPrice();
		
		int i = 0;
		for (Product product : products) {
			
			if (product.getStock() != null) {
				int id = product.getStock().getId();
				Stock stocks = stockRepo.findById(id).get();
				stocks.setTaxInPercentage("" + taxInPercentageArray[i]);
				int oldQty = Integer.parseInt(stocks.getQuantity());

				int newQty = quantityArray[i];
				String addQty = String.valueOf(oldQty + newQty);

				stocks.setQuantity(addQty);
				stockRepo.save(stocks);

			} else {
				Stock stock = new Stock();
				stock.setTaxInPercentage("" + taxInPercentageArray[i]);
				stock.setQuantity("" + quantityArray[i]);
				stock.setMinQuantity("10");

				stockRepo.save(stock);

				product.setStock(stock);
				
			}
			product.setPrice(prices.get(i));
			productRepo.save(product);
			i++;
		}

		return "redirect:/a2zbilling/admin/purchasebill/transection";
	}

	@GetMapping("/purchasebill/update/{id}")
	public String updatePurchaseBill(@PathVariable("id") int id, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		PartiesTransaction partiesTransactions = partiesTransectionRepo.findById(id).get();
		model.addAttribute("partiesTransactions", partiesTransactions);

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasebill_update";
	}

	@GetMapping("/purchaseorder/add")
	public String addPurchaseOrder(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		int userId = user.getId();

		// to render Supplier's on Purchase order -> Here Parties means Supplier
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

		// PurchaseOrder No
		String pobillNo = purchaseOrderRepo.maxPurchaseOrderNo(userId);
		if (pobillNo != null && !pobillNo.isEmpty()) {
			String newpoBillNo = pobillNo.substring(0, 5);
			int no = Integer.parseInt(pobillNo.substring(5, pobillNo.length()));
			no += 1;
			newpoBillNo += no;
			model.addAttribute("newpoBillNo", newpoBillNo);
		} else {
			String newpoBillNo = "PO - 1";
			model.addAttribute("newpoBillNo", newpoBillNo);
		}

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchaseorder_add";
	}

	@PostMapping("/purchaseorder/add")
	public String processPurchaseOrder(@ModelAttribute PurchaseOrder purchaseOrder) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		int userId = user.getId();
		Company company = companyRepo.getCompanyByUserId(user.getId());

		purchaseOrder.setStatus("Active");
		user.getPurchaseorder().add(purchaseOrder);
		purchaseOrder.setUser(user);
		purchaseOrderRepo.save(purchaseOrder);

		return "redirect:/a2zbilling/admin/purchaseorder/transection";
	}
  
	@GetMapping("/purchaseorder/transection")
	public String purchaseOrderList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

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

		Pageable pageable = PageRequest.of(page, size);
		Page<PurchaseOrder> purchaseOrders = purchaseOrderRepo.showAllActivePurchaseOrderTransection(userId, pageable);
		model.addAttribute("purchaseOrders", purchaseOrders);
		model.addAttribute("currentPage", page);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchaseorder_transection";
	}

	@GetMapping("/purchaseorder/delete/{id}")
	public String deletePurchaseorderById(@PathVariable("id") int id) {

		Optional<PurchaseOrder> po = purchaseOrderRepo.findById(id);
		PurchaseOrder purchaseOrder = po.get();

		purchaseOrder.setStatus("InActive");
		purchaseOrderRepo.save(purchaseOrder);

		return "redirect:/a2zbilling/admin/purchaseorder/transection";
	}

	@GetMapping("/purchasereturn/transection")
	public String purchaseReturnList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
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

		Pageable pageable = PageRequest.of(page, size);
		Page<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllActivePartiesTransection1(userId,
				pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);
		model.addAttribute("currentPage", page);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasereturn_transection";

	}

	@GetMapping("/purchasereturn/add/{id}")
	public String addPurchaseReturn(Model model, @PathVariable int id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		Company company = companyRepo.getCompanyByUserId(user.getId());

		PartiesTransaction partiesTransaction = partiesTransectionRepo.findById(id).get();
		model.addAttribute("partiesTransaction", partiesTransaction);

		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier(userId);
		model.addAttribute("suppliers", suppliers);

		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.showAllActiveUnit(userId);
		model.addAttribute("units", units);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/purchasereturn_add";

	}

	@PostMapping("/purchasereturn/add")
	public String addPurchaseReturnProccess(@ModelAttribute PartiesTransaction partiesTransaction, Model model,
			HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		PartiesTransaction partiesTransactions = partiesTransectionRepo.findById(partiesTransaction.getId()).get();
		Double oldDue = Double.parseDouble(partiesTransactions.getDues());
		Double newDue = Double.parseDouble(partiesTransaction.getDues());
		
		List<Product> newproducts = partiesTransaction.getProducts();
		List<Product> oldproducts = partiesTransactions.getProducts();

		String newQuantity = partiesTransaction.getQuantity();
		int[] newQuantityArray = Arrays.stream(newQuantity.split(",")).mapToInt(Integer::parseInt).toArray();
		String oldQuantity = partiesTransactions.getQuantity();
		int[] oldQuantityArray = Arrays.stream(oldQuantity.split(",")).mapToInt(Integer::parseInt).toArray();
		int k = 0;
		for (Product product : newproducts) {
			int index = oldproducts.indexOf(product);
			Stock stock = product.getStock();
			int AQty = Integer.parseInt(stock.getQuantity());
			int Qty = oldQuantityArray[index] - newQuantityArray[k];
			if (AQty < Qty) {
				session.setAttribute("message", "Product Return Quantity is Less than Available Quantity !!");
				return "redirect:/a2zbilling/admin/purchasebill/transection";
			}
			k++;
		}

		String size = partiesTransaction.getSize();
		String[] oldSizeArray = size.split(",");

		PartiesTransaction partiesTransaction1 = new PartiesTransaction();

		partiesTransaction1.setBillNo(partiesTransaction.getBillNo());
		partiesTransaction1.setDate(partiesTransaction.getDate());
		partiesTransaction1.setPaymentMode(partiesTransaction.getPaymentMode());

		int j = 0;
		for (Product product : oldproducts) {
			if (newproducts.contains(product)) {
				int index = partiesTransaction.getProducts().indexOf(product);
				String newQuantity1 = partiesTransaction.getQuantity();
				int[] newQuantityArray1 = Arrays.stream(newQuantity1.split(",")).mapToInt(Integer::parseInt).toArray();

				if (oldQuantityArray[j] > newQuantityArray1[index]) {

					if (partiesTransaction1.getProducts().size() == 0) {
						List<Product> productList = new ArrayList<>();
						productList.add(product);
						partiesTransaction1.setProducts(productList);
					} else {
						partiesTransaction1.getProducts().add(product);
					}

					if (partiesTransaction1.getQuantity() != null) {
						partiesTransaction1.setQuantity(partiesTransaction1.getQuantity() + ","
								+ String.valueOf(oldQuantityArray[j] - newQuantityArray1[index]));
					} else
						partiesTransaction1.setQuantity(String.valueOf(oldQuantityArray[j] - newQuantityArray1[index]));

					if (partiesTransaction1.getSize() != null) {
						partiesTransaction1.setSize(partiesTransaction1.getSize() + "," + oldSizeArray[j]);
					} else
						partiesTransaction1.setSize(oldSizeArray[j]);

					int diff = oldQuantityArray[j] - newQuantityArray1[index];
					Stock stock = product.getStock();
					int quantity = Integer.parseInt(stock.getQuantity()) - diff;
					stock.setQuantity(String.valueOf(quantity));
					stockRepo.save(stock);
					productRepo.save(product);
				}
			} else {
				if (partiesTransaction1.getProducts().size() == 0) {
					List<Product> productList = new ArrayList<>();
					productList.add(product);
					partiesTransaction1.setProducts(productList);

				} else {
					partiesTransaction1.getProducts().add(product);
				}

				if (partiesTransaction1.getQuantity() != null) {
					partiesTransaction1
							.setQuantity(partiesTransaction1.getQuantity() + "," + String.valueOf(oldQuantityArray[j]));
				} else
					partiesTransaction1.setQuantity(String.valueOf(oldQuantityArray[j]));

				if (partiesTransaction1.getSize() != null) {
					partiesTransaction1.setSize(partiesTransaction1.getSize() + "," + oldSizeArray[j]);
				} else
					partiesTransaction1.setSize(oldSizeArray[j]);

				Stock stock = product.getStock();
				int quantity = Integer.parseInt(stock.getQuantity()) - oldQuantityArray[j];
				stock.setQuantity(String.valueOf(quantity));
				stockRepo.save(stock);
				productRepo.save(product);
			}
			j++;
		}
		if (partiesTransaction1.getProducts().size() > 0) {
			double netPayment = 0;
			String newQuantity1 = partiesTransaction1.getQuantity();
			int[] newQuantityArray1 = Arrays.stream(newQuantity1.split(",")).mapToInt(Integer::parseInt).toArray();
			List<Product> productList = partiesTransaction1.getProducts();
			k = 0;
			for (Product product : productList) {
				double price = Integer.parseInt(product.getPrice());
				double total = price * newQuantityArray1[k];
				netPayment += total;
				k++;
			}
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			if(partiesTransaction.getPaymentStatus().equals("Amount Not Received")) {
				
				Parties parties = partiesTransaction.getParties();
				Double amount = Double.parseDouble(parties.getOpeningBalance()) - oldDue + newDue + netPayment;
				String stringAmount = decimalFormat.format(amount);
				if (amount > 0) {
					parties.setPayment("toReceive");
				} else {
					parties.setPayment("toPay");
				}
				parties.setOpeningBalance(stringAmount);
				partiesRepo.save(parties);
			} else {
				Parties parties = partiesTransaction.getParties();
				Double amount = Double.parseDouble(parties.getOpeningBalance()) - oldDue + newDue;
				String stringAmount = decimalFormat.format(amount);
				if (amount > 0) {
					parties.setPayment("toReceive");
				} else {
					parties.setPayment("toPay");
				}
				parties.setOpeningBalance(stringAmount);
				partiesRepo.save(parties);
			}
			String stringNetPayment = decimalFormat.format(netPayment);
			partiesTransaction1.setParties(partiesTransaction.getParties());
			partiesTransaction1.setPaymentStatus(partiesTransaction.getPaymentStatus());
			partiesTransaction1.setNetPayment(stringNetPayment);
			partiesTransaction1.setStatus("Active");
			partiesTransaction1.setPurchaseType("Return");
			partiesTransaction1.setUser(user);
			session.setAttribute("message", "Purchase Return Successfully !!");
			partiesTransectionRepo.save(partiesTransaction1);
		}

		partiesTransactions.setDate(partiesTransaction.getDate());
		partiesTransactions.setQuantity(partiesTransaction.getQuantity());
		partiesTransactions.setDiscountInRupees(partiesTransaction.getDiscountInRupees());
		partiesTransactions.setDiscountInPercentage(partiesTransaction.getDiscountInPercentage());
		partiesTransactions.setTaxInRupees(partiesTransaction.getTaxInRupees());
		partiesTransactions.setTaxInPercentage(partiesTransaction.getTaxInPercentage());
		partiesTransactions.setPaymentMode(partiesTransaction.getPaymentMode());
		partiesTransactions.setPaid(partiesTransaction.getPaid());
		partiesTransactions.setDues(partiesTransaction.getDues());
		partiesTransactions.setTotalAmount(partiesTransaction.getTotalAmount());
		partiesTransactions.setNetPayment(partiesTransaction.getNetPayment());
		partiesTransactions.setSize(partiesTransaction.getSize());
		partiesTransactions.setParties(partiesTransaction.getParties());
		partiesTransactions.setProducts(partiesTransaction.getProducts());
		partiesTransactions.setUser(user);

		partiesTransactions.setPurchaseType("Purchase");
		partiesTransactions.setStatus("Active");
		partiesTransectionRepo.save(partiesTransactions);
		session.setAttribute("message", "Purchase Updated Successfully !!");
		return "redirect:/a2zbilling/admin/purchasebill/transection";

	}

	@GetMapping("/purchasereturn/update")
	public String updatePurchaseReturn(Model model) {
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

		return "admin/purchasereturn_update";
	}

	@GetMapping("/purchasereturn/delete/{id}")
	public String deletePurchasebillById(@PathVariable("id") int id) {

		PartiesTransaction partiesTransaction = partiesTransectionRepo.findById(id).get();
		partiesTransaction.setStatus("InActive");
		partiesTransectionRepo.save(partiesTransaction);

		return "redirect:/a2zbilling/admin/purchasereturn/transection";
	}

	@GetMapping("/sales/list")
	public String salesList(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
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

		Pageable pageable = PageRequest.of(page, size);
		Page<Sales> sales = salesRepo.showAllActive(userId, pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		List<Customer> customers = customerRepo.findAll();
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/sales_list";
	}

	@GetMapping("/sales/add")
	public String addSales(Model model) {
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

		List<Customer> customers = customerRepo.showAllCustomerBYActive(userId);
		model.addAttribute("customers", customers);

		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		String salesBillString = salesRepo.maxSalesBillNo(userId);
		if (salesBillString != null && !salesBillString.isEmpty()) {
			String newSaleBillNo = salesBillString.substring(0, 5);
			int no = Integer.parseInt(salesBillString.substring(5, salesBillString.length()));
			no += 1;
			newSaleBillNo += no;
			model.addAttribute("newSaleBillNo", newSaleBillNo);
		} else {

			String newSaleBillNo = "SB - 1";
			model.addAttribute("newSaleBillNo", newSaleBillNo);
		}

		List<Charges> charges = chargesRepo.findByActiveCharges(userId);
		model.addAttribute("charges", charges);

		String signature = company.getSignature();
		if (signature != null) {
			String companySignImgPath = StringUtils.ImagePaths.companySignImageUrl + signature;
			model.addAttribute("companySignImgPath", companySignImgPath);
		}

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/sales_add";

	}

	@PostMapping("/sales/add")
	public String salesAddingProcess(@ModelAttribute Sales sales, HttpSession session, HttpServletRequest request, Model model) throws URISyntaxException {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userRepo.findByUsername(auth.getName());

	    String referer = request.getHeader("referer");
	    java.net.URI uri = new java.net.URI(referer);
	    String path = uri.getPath();
	    String query = uri.getQuery();
	    String endpoint = path + (query != null ? "?" + query : "");

	    if (sales.getDiscountInAmount() == null || sales.getDiscountInAmount().isEmpty()) {
	        sales.setDiscountInAmount("0");
	    }
	    if (sales.getDiscountInPercentage() == null || sales.getDiscountInPercentage().isEmpty()) {
	        sales.setDiscountInPercentage("0");
	    }
	    if (sales.getSignatureImage() == null) {
	        sales.setSignatureImage("");
	    }
	    
	    sales.setUser(user);
	    sales.setStatus("Active");
	    sales.setSalesType("Sale");
	    user.getSales().add(sales);
	    salesRepo.save(sales);
	    
	    List<Charges> charges = sales.getCharges();
	    for (Charges charge : charges) {
	        charge.getSales().add(sales);
	        chargesRepo.save(charge);
	    }

	    List<Product> products = sales.getProducts();
	    String quantity = sales.getQuantity();
	    int[] quantityArray = Arrays.stream(quantity.split(",")).mapToInt(Integer::parseInt).toArray();
	    int i = 0;
	    for (Product product : products) {
	        Stock stock = product.getStock();
	        int num = Integer.parseInt(stock.getQuantity()) - quantityArray[i];
	        stock.setQuantity(String.valueOf(num));
	        stockRepo.save(stock);
	        product.setStock(stock);

	        product.getSales().add(sales);
	        
	        // Save stock changes and update product-customer relation
	        productRepo.save(product);
	        i++;
	    }
	    DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
	    Customer customer = sales.getCustomer();
	    customer.getSales().add(sales);
	    Double amount = Double.parseDouble(customer.getDueAmount()) + Double.parseDouble(sales.getDueAmount());
	    String due = decimalFormat.format(amount);
	    customer.setDueAmount(due);
	    
	    // Save final customer state
	    customerRepo.save(customer);
	    
	    // Save final user state
	    userRepo.save(user);
	    
	    session.setAttribute("message", "Sales Bill Generated Successfully");

	    return "redirect:" + endpoint;
	}


	@GetMapping("/sales/update")
	public String updatesales(Model model) {

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

		return "admin/sales_update";
	}

	@GetMapping("/sales/delete/{id}")
	public String deleteSales(@PathVariable("id") int id, HttpServletRequest request) throws URISyntaxException {

		Sales sales = salesRepo.findById(id).get();

		sales.setStatus("InActive");

		salesRepo.save(sales);
		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");

		return "redirect:" + endpoint;
	}

	// create by Mahesh
	@GetMapping("/sales/return/list")
	public String returnSalesList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
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

		Pageable pageable = PageRequest.of(page, size);
		Page<Sales> sales = salesRepo.showAllActiveSalesReturn(userId, pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		List<Customer> customers = customerRepo.findAll();
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/saleReturnList";
	}

	// create by Mahesh
	@GetMapping("/sales/return/{id}")
	public String returnsales(@PathVariable("id") int id, Model model) {
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

		List<Customer> customers = customerRepo.showAllCustomerBYActive(userId);
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

		List<Charges> charges = chargesRepo.findByActiveCharges(userId);
		model.addAttribute("charges", charges);

		Sales sale = salesRepo.findById(id).get();
		model.addAttribute("sale", sale);
		String signature = company.getSignature();
		if (signature != null) {
			String companySignImgPath = StringUtils.ImagePaths.companySignImageUrl + signature;
			model.addAttribute("companySignImgPath", companySignImgPath);
		}

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/sales_return";
	}

	@PostMapping("/sales/return")
	public String returnsalesProcess(@ModelAttribute Sales sale, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Sales sales = salesRepo.findById(sale.getId()).get();

		if (sale.getProducts().size() == 0) {
			sales.setReturnPaidStatus(sale.getReturnPaidStatus());
			sales.setSalesType("Return");
			sales.setStatus("Active");
			salesRepo.save(sales);
			return "redirect:/a2zbilling/admin/sales/list";
		}
		// this code for check product is return or not
		Sales sale1 = new Sales();
		sale1.setCustomer(sales.getCustomer());
		sale1.setSaleBillNo(sales.getSaleBillNo());
		sale1.setDate(sales.getDate());

		String size1 = sales.getSize();
		String[] oldSizeArray1 = size1.split(",");
		String oldQuantity1 = sales.getQuantity();
		int[] oldQuantityArray1 = Arrays.stream(oldQuantity1.split(",")).mapToInt(Integer::parseInt).toArray();
		String tax1 = sales.getTaxInPercentage();
		String[] oldTaxArray1 = tax1.split(",");
		String taxInAmount1 = sales.getTaxInAmount();
		String[] oldTaxInAmountArray1 = taxInAmount1.split(",");
		int j = 0;

		for (Product product : sales.getProducts()) {
			if (sale.getProducts().contains(product)) {
				int index = sale.getProducts().indexOf(product);
				String newQuantity1 = sale.getQuantity();
				int[] newQuantityArray1 = Arrays.stream(newQuantity1.split(",")).mapToInt(Integer::parseInt).toArray();

				if (oldQuantityArray1[j] > newQuantityArray1[index]) {

					sale1.getProducts().add(product);

					if (sale1.getQuantity() != null) {
						sale1.setQuantity(sale1.getQuantity() + ","
								+ String.valueOf(oldQuantityArray1[j] - newQuantityArray1[index]));
					} else
						sale1.setQuantity(String.valueOf(oldQuantityArray1[j] - newQuantityArray1[index]));

					if (sale1.getSize() != null) {
						sale1.setSize(sale1.getSize() + "," + oldSizeArray1[j]);
					} else
						sale1.setSize(oldSizeArray1[j]);

					sale1.setPaymentMode(sales.getPaymentMode());

					if (sale1.getTaxInPercentage() != null) {
						sale1.setTaxInPercentage(sale1.getTaxInPercentage() + "," + oldTaxArray1[j]);
					} else
						sale1.setTaxInPercentage(oldTaxArray1[j]);

					if (sale1.getTaxInAmount() != null) {
						sale1.setTaxInAmount(sale1.getTaxInAmount() + "," + oldTaxInAmountArray1[j]);
					} else
						sale1.setTaxInAmount(oldTaxInAmountArray1[j]);
				}
			} else {

				sale1.getProducts().add(product);
				if (sale1.getQuantity() != null) {
					sale1.setQuantity(sale1.getQuantity() + "," + String.valueOf(oldQuantityArray1[j]));
				} else
					sale1.setQuantity(String.valueOf(oldQuantityArray1[j]));

				if (sale1.getSize() != null) {
					sale1.setSize(sale1.getSize() + "," + oldSizeArray1[j]);
				} else
					sale1.setSize(oldSizeArray1[j]);

				sale1.setPaymentMode(sales.getPaymentMode());

				if (sale1.getTaxInPercentage() != null) {
					sale1.setTaxInPercentage(sale1.getTaxInPercentage() + "," + oldTaxArray1[j]);
				} else
					sale1.setTaxInPercentage(oldTaxArray1[j]);

				if (sale1.getTaxInAmount() != null) {
					sale1.setTaxInAmount(sale1.getTaxInAmount() + "," + oldTaxInAmountArray1[j]);
				} else
					sale1.setTaxInAmount(oldTaxInAmountArray1[j]);
			}
			j++;
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		double netPayment = 0;
		if (sale1.getProducts().size() > 0) {
			
			String newQuantity1 = sale1.getQuantity();
			int[] newQuantityArray1 = Arrays.stream(newQuantity1.split(",")).mapToInt(Integer::parseInt).toArray();
			
			List<Product> productList = sale1.getProducts();
			int k = 0;
			for (Product product : productList) {
				String priceInString = "";
				if(product.getSellingPrice() == null)
				{
					priceInString = product.getPrice();
				} else {
					priceInString = product.getSellingPrice();
				}
				double price = Double.parseDouble(priceInString);
				double total = price * newQuantityArray1[k];
				netPayment += total;
				k++;
			}
			
			String  stringNetAmount = decimalFormat.format(netPayment);
			
			sale1.setSignatureImage(sale.getSignatureImage());
			sale1.setReturnPaidStatus(sale.getReturnPaidStatus());
			sale1.setNetPayment(stringNetAmount);
			sale1.setSalesType("Return");
			sale1.setStatus("Active");
			sale1.setUser(user);
			salesRepo.save(sale1);
		}
		// this is the end of the sales return

		// new products and customer
		Customer newCustomer = sale.getCustomer();
		List<Product> newProducts = sale.getProducts();

		// old products and customer
		List<Product> oldProduct = sales.getProducts();
		Customer oldCustomer = sales.getCustomer();

		// update the due amount
		Double oldDue = Double.parseDouble(sales.getDueAmount());
		Double newDue = Double.parseDouble(sale.getDueAmount());

		if (oldCustomer.getId() == newCustomer.getId()) {
			if(sale.getReturnPaidStatus().equals("Amount Not Paid")) {
				Double amount = Double.parseDouble(oldCustomer.getDueAmount()) - oldDue + newDue - netPayment;
				String stringAmount = decimalFormat.format(amount);
				oldCustomer.setDueAmount(stringAmount);
				customerRepo.save(oldCustomer);
			} else {
				Double amount = Double.parseDouble(oldCustomer.getDueAmount()) - oldDue + newDue;
				String stringAmount = decimalFormat.format(amount);
				oldCustomer.setDueAmount(stringAmount);
				customerRepo.save(oldCustomer);
			}
		}
		
		// update the quantity
		String oldQuantity = sales.getQuantity();
		int[] oldQuantityArray = Arrays.stream(oldQuantity.split(",")).mapToInt(Integer::parseInt).toArray();
		String newQuantity = sale.getQuantity();
		int[] newQuantityArray = Arrays.stream(newQuantity.split(",")).mapToInt(Integer::parseInt).toArray();

		int i = 0;
		for (Product product : oldProduct) {
			Stock stock = product.getStock();

			int num = Integer.parseInt(stock.getQuantity()) + oldQuantityArray[i];
			stock.setQuantity(String.valueOf(num));
			product.setStock(stock);

			productRepo.save(product);
		}
		i = 0;
		for (Product product : newProducts) {
			Stock stock = product.getStock();

			int num = Integer.parseInt(stock.getQuantity()) - newQuantityArray[i];
			stock.setQuantity(String.valueOf(num));
			product.setStock(stock);

			newCustomer.getProducts().add(product);
			product.getCustomer().add(newCustomer);
			productRepo.save(product);
		}

		sales.setDate("");
		sales.setDate(sale.getDate());
		sales.setQuantity(sale.getQuantity());
		sales.setTaxInAmount(sale.getTaxInAmount());
		sales.setTaxInPercentage(sale.getTaxInPercentage());

		if (sales.getDiscountInAmount() == "") {
			sales.setDiscountInAmount("0");
		} else {
			sales.setDiscountInAmount(sale.getDiscountInAmount());
		}
		if (sales.getDiscountInPercentage() == "") {
			sales.setDiscountInPercentage("0");
		} else {
			sales.setDiscountInPercentage(sale.getDiscountInPercentage());
		}

		sales.setPaymentMode(sale.getPaymentMode());
		sales.setAmountPaid(sale.getAmountPaid());
		sales.setDueAmount(sale.getDueAmount());
		sales.setNetPayment(sale.getNetPayment());
		sales.setTotalAmount(sale.getTotalAmount());
		sales.setSignatureImage(sale.getSignatureImage());
		sales.setCharges(sale.getCharges());
		sales.setSize(sale.getSize());

		sales.setProducts(newProducts);
		sales.setCustomer(newCustomer);
		sales.setSalesType("Sale");
		salesRepo.save(sales);
		return "redirect:/a2zbilling/admin/sales/list";
	}

	// Created by Younus
	@GetMapping("/Item/add")
	public String addItem(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(userId);
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		// To get product Name data from db
		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/Item_add";

	}

	// Created by Younus
	@GetMapping("/managestock")
	public String manageStock(Model model) {
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

		int id = user.getId();
		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier(id);
		model.addAttribute("suppliers", suppliers);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);

		List<Category> categorys = categoryRepo.findByActiveCategory(userId);
		model.addAttribute("categorys", categorys);

		List<Brand> brands = brandRepo.showAllActiveBrand(userId);
		model.addAttribute("brands", brands);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/manage_stock";
	}

	// Created by Younus
	@PostMapping("/managestock")
	public String manageStockProcess(@ModelAttribute Stock stock, @RequestParam("sellingPrice") double sellingPrice, HttpSession session, Model model,
			HttpServletRequest request) throws URISyntaxException {

		Product product = stock.getProduct();
		product.setSellingPrice(String.valueOf(sellingPrice));
		if (product.getStock() != null) {
			int id = product.getStock().getId();
			Stock stocks = stockRepo.findById(id).get();

			stocks.setMinQuantity(stock.getMinQuantity());
			productRepo.save(product);
			stockRepo.save(stocks);
		} else {
			stockRepo.save(stock);

			product.setStock(stock);
			productRepo.save(product);
		}

		session.setAttribute("message", "Stock Added Or Updated Successfully");

		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		String endpoint = path + (query != null ? "?" + query : "");
		return "redirect:" + endpoint;
	}

	// Created by Younus - get Brand list
	@GetMapping("/brand/list")
	public String brandList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		// Pagination Added
		Pageable pageable = PageRequest.of(page, size);
		Page<Brand> brands = brandRepo.showAllActiveBrand(userId, pageable);
		model.addAttribute("brands", brands);
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
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/brand_list";
	}

	// Created by Younus - brand add Process
	@PostMapping("brand/add")
	public String brandaddProcess(@ModelAttribute BrandDto brandDto, Model model, HttpServletRequest request,
			HttpSession session) throws URISyntaxException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		// Create a new Brand instance
		Brand brand = new Brand();
		brand.setName(brandDto.getName());

		// Get the uploaded logo file from the DTO
		MultipartFile file = brandDto.getLogo();

		// Generate a unique file name using the current timestamp and the original file
		// name
		Date date = new Date();
		String storageFileName = date.getTime() + "_" + file.getOriginalFilename();

		// Define the directory to upload the logo file
		String uploadDir = "src/main/resources/static/img/brandlogo/";
		Path uploadPath = Paths.get(uploadDir);

		// Create the upload directory if it doesn't exist
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Save the uploaded file to the defined directory
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
		}

		// Set the logo filename in the Brand instance
		brand.setLogo(storageFileName);

		// Set the status of the brand to "Active"
		brand.setStatus("Active");
		List<Brand> brands = user.getBrand();
		user.setBrand(brands);
		brand.setUser(user);

		// Save the brand to the repository
		brandRepo.save(brand);
		userRepo.save(user);

		/*
		 * Brand nFound=brandRepo.findByName(brand.getName());
		 * 
		 * if (nFound == null) { brand.setStatus("Active"); brand.setUser(user);
		 * brandRepo.save(brand); session.setAttribute("message",
		 * "Brand added successfully!"); } else if
		 * (brand.getName().equals(nFound.getName())) { session.setAttribute("message",
		 * "Brand already exists!"); }
		 */

		// Get the referer URL from the request header
		String referer = request.getHeader("referer");
		// Parse the referer URL to get the path and query
		java.net.URI uri = new java.net.URI(referer);
		String path = uri.getPath();
		String query = uri.getQuery();
		// Construct the redirect URL with the original path and query parameters
		String endpoint = path + (query != null ? "?" + query : "");
		// Redirect to the referer URL
		return "redirect:" + endpoint;
	}

	@GetMapping("/brand/update/{id}")
	public String updateBrandList(@PathVariable int id, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		Brand brand = brandRepo.findById(id).get();
		model.addAttribute("brand", brand);

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);
		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/brand_update";
	}

	// Created by Younus - update brand Process
	@PostMapping("/brand/update")
	public String updateBrandListProcess(@ModelAttribute BrandDto brandDto) throws IOException {
		Brand brand = brandRepo.findById(brandDto.getId()).get();
		brand.setName(brandDto.getName());

		// Get the uploaded logo file from the DTO
		MultipartFile file = brandDto.getLogo();

		// Generate a unique file name using the current timestamp and the original file
		// name
		Date date = new Date();
		String storageFileName = date.getTime() + "_" + file.getOriginalFilename();

		// Define the directory to upload the logo file
		String uploadDir = "src/main/resources/static/img/brandlogo/";
		Path uploadPath = Paths.get(uploadDir);

		// Create the upload directory if it doesn't exist
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Save the uploaded file to the defined directory
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
		}

		// Set the logo filename in the Brand instance
		brand.setLogo(storageFileName);
		brandRepo.save(brand);
		return "redirect:/a2zbilling/admin/brand/list";
	}

	// Created by Younus -for soft delete (Status - Inactive)
	@GetMapping("/brand/delete/{id}")
	public String deleteBrandById(@PathVariable("id") int id) {

		Brand brand = brandRepo.findById(id).get();
		brand.setStatus("InActive");
		brandRepo.save(brand);

		return "redirect:/a2zbilling/admin/brand/list";
	}

	// Created by Younus
	@GetMapping("/purchaseReport")
	public String purchaseReport(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		// To get Parties Name data from db
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		// Pagination Added
		Pageable pageable = PageRequest.of(page, size);
		Page<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllActivePartiesTransection(userId,
				pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);
		model.addAttribute("currentPage", page);

		Company company = companyRepo.getCompanyByUserId(userId);
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		// from date to End Date
		if (startDate != null && endDate != null) {
			// If date range is provided, filter the transactions
			partiesTransactions = partiesTransectionRepo.findByUserIdAndDateBetween(userId, startDate, endDate,
					pageable);
		} else {
			// If no date range, show all transactions
			partiesTransactions = partiesTransectionRepo.showAllActivePartiesTransection(userId, pageable);
		}

		return "admin/purchase_Report";
	}

	// Created by Younus
	@GetMapping("/salesReport")
	public String salesReport(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		int userId = user.getId();
		// To get sale data from db

		// Pagination Added
		Pageable pageable = PageRequest.of(page, size);
		Page<Sales> sales = salesRepo.showAllActiveSales(userId, pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		// from date to End Date
		if (startDate != null && endDate != null) {
			// If date range is provided, filter the transactions
			sales = salesRepo.findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
		} else {
			// If no date range, show all transactions
			sales = salesRepo.showAllActiveSales(userId, pageable);
		}

		return "admin/sales_Report";
	}

	// Created by Younus
	@GetMapping("/purchaseTaxReport")
	public String purchaseTaxReport(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		int userId = user.getId();

		// To get Parties Name data from db
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		// Pagination Added
		Pageable pageable = PageRequest.of(page, size);
		Page<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllActivePartiesTransection(userId,
				pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);
		model.addAttribute("currentPage", page);

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

		// from date to End Date
		if (startDate != null && endDate != null) {
			// If date range is provided, filter the transactions
			partiesTransactions = partiesTransectionRepo.findByUserIdAndDateBetween(userId, startDate, endDate,
					pageable);
		} else {
			// If no date range, show all transactions
			partiesTransactions = partiesTransectionRepo.showAllActivePartiesTransection(userId, pageable);
		}

		return "admin/purchase_Tax_Report";
	}

	// Created by Younus salesTaxReport
	@GetMapping("/salesTaxReport")
	public String salesTaxReport(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		// To get sale data from db
		int Userid = user.getId();

		// Pagination Added
		Pageable pageable = PageRequest.of(page, size);
		Page<Sales> sales = salesRepo.showAllActiveSales(Userid, pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);
		model.addAttribute("user", user);
		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/sales_Tax_Report";
	}

	// Created by Younus - get CashInHand report
	@GetMapping("/cashPaymentList")
	public String cashInHand(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		// Pagination Added
//			Pageable pageable = PageRequest.of(page, size);
//			Page<Sales> sales = salesRepo.showAllActiveSales(userId, pageable);
//			model.addAttribute("sales", sales);
//			model.addAttribute("currentPage", page);

		List<Sales> sales = salesRepo.showAllCashPayment(userId);
		model.addAttribute("sales", sales);

		List<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllCashPayment(userId);
		model.addAttribute("partiesTransactions", partiesTransactions);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);
		model.addAttribute("user", user);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/cashPaymentList";
	}

	@GetMapping("/chequePaymentList")
	public String chequePaymentList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Pageable pageable = PageRequest.of(page, size);
		Page<Sales> sales = salesRepo.showAllChequePayment(userId, pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);

		Page<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllChequePayment(userId, pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);
		model.addAttribute("user", user);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/chequePaymentList";
	}

	@GetMapping("/onlinePaymentList")
	public String onlinePaymentList(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Pageable pageable = PageRequest.of(page, size);
		Page<Sales> sales = salesRepo.showAllOnlinePayment(userId, pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);

		Page<PartiesTransaction> partiesTransactions = partiesTransectionRepo.showAllOnlinePayment(userId, pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);

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

		return "admin/onlinePaymentList";
	}

	@GetMapping("/profitAndLossReport")
	public String profitAndLossReport(Model model, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		String username = user.getUsername();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		// To get Parties Name data from db
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);
		
		double sumOfAllActivePurchase = 0;
		double sumOfAllActivePurchaseReturn = 0;
		double sumOfAllActiveSales = 0;
		double sumOfAllActiveSalesReturn = 0;
		double sumOfAllActiveExpense = 0;
		
		List<PartiesTransaction> partiesTransactions1 = partiesTransectionRepo.showAllActivePartiesTransection(userId);
		List<PartiesTransaction> partiesTransactions2 = partiesTransectionRepo.showAllActivePartiesTransection1(userId);
		List<Sales> sales1 = salesRepo.showAllActiveSales(userId);
		List<Sales> sales2 = salesRepo.showAllActiveSalesReturn(userId);
		List<Expense> expences = expenseRepo.showAllActiveExpenseList(userId);
		
		for(PartiesTransaction partiesTransaction : partiesTransactions1) {
			if(partiesTransaction.getNetPayment() != null) sumOfAllActivePurchase += Float.parseFloat(partiesTransaction.getNetPayment());
		}
		for(PartiesTransaction partiesTransaction : partiesTransactions2) {
			if(partiesTransaction.getNetPayment() != null) sumOfAllActivePurchaseReturn += Float.parseFloat(partiesTransaction.getNetPayment());
			
		}
		for(Sales sale : sales1) {
			if(sale.getNetPayment() != null) sumOfAllActiveSales += Float.parseFloat(sale.getNetPayment());
		}
		for(Sales sale : sales2) {
			if(sale.getNetPayment() != null) sumOfAllActiveSalesReturn += Float.parseFloat(sale.getNetPayment());
		}
		for(Expense expense :expences) {
			if(expense.getNetPayment() != null) sumOfAllActiveExpense += Float.parseFloat(expense.getNetPayment());
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
       
		String sumOfAllPurchase = decimalFormat.format(sumOfAllActivePurchase);
		String sumOfAllPurchaseReturn = decimalFormat.format(sumOfAllActivePurchaseReturn);
		String sumOfAllSale = decimalFormat.format(sumOfAllActiveSales);
		String sumOfAllSaleReturn = decimalFormat.format(sumOfAllActiveSalesReturn);
		String sumOfAllExpense = decimalFormat.format(sumOfAllActiveExpense);
		
		model.addAttribute("sumOfAllPurchase", sumOfAllPurchase);
		model.addAttribute("sumOfAllPurchaseReturn", sumOfAllPurchaseReturn);
		model.addAttribute("sumOfAllSale", sumOfAllSale);
		model.addAttribute("sumOfAllSaleReturn", sumOfAllSaleReturn);
		model.addAttribute("sumOfAllExpense", sumOfAllExpense);
		
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

		return "admin/profit_And_Loss_Report";
	}

	@GetMapping("/expense/add")
	public String expenseAdd(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
	
		int userId = user.getId();

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);
		
		// Expense Bill No
		String expensebillNo=expenseRepo.maxExpenseBillNo(userId);
		if(expensebillNo != null && !expensebillNo.isEmpty()) {
			String newexpensebillNo=expensebillNo.substring(0, 5);
			int no = Integer.parseInt(expensebillNo.substring(5, expensebillNo.length()));
			no += 1;
			newexpensebillNo +=no;
			model.addAttribute("newexpensebillNo", newexpensebillNo);
			
		}else {
			String newexpensebillNo ="EB - 1";
			model.addAttribute("newexpensebillNo", newexpensebillNo);
		}
		
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}

		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "/admin/add_expense";
	}

	@PostMapping("/expense/add")
	public String processAddExpense(@ModelAttribute Expense expense) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		int userId = user.getId();
		Company company = companyRepo.getCompanyByUserId(user.getId());

		expense.setStatus("Active");
		user.getExpense().add(expense);
		expense.setUser(user);
		expenseRepo.save(expense);

		return "redirect:/a2zbilling/admin/expense/list";
	}

	@GetMapping("/expense/list")
	public String getAllExpense(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Pageable pageable = PageRequest.of(page, size);
		Page<Expense> expenses = expenseRepo.showAllActiveExpenseList(userId, pageable);
		model.addAttribute("expenses", expenses);
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

		return "admin/expense_list";
	}
	
	@GetMapping("/expense/delete/{id}")
	public String deleteExpenseById(@PathVariable("id") int id) {
		
        Optional<Expense> e=expenseRepo.findById(id);
        Expense expense=e.get();
        
        expense.setStatus("InActive");
        expenseRepo.save(expense);

		return "redirect:/a2zbilling/admin/expense/list";
	}
}
