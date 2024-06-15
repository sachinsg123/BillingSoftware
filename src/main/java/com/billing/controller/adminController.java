package com.billing.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.billing.model.GSTRate;
import com.billing.model.Parties;
import com.billing.model.PartiesTransaction;
import com.billing.model.Product;
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
import com.billing.repositories.GSTRepository;
import com.billing.repositories.PartiesRepository;
import com.billing.repositories.PartiesTransectionRepository;
import com.billing.repositories.ProductRepository;
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
public class adminController{

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
	    
	    if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
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
	    if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
	    
		MultipartFile image = userDto.getImageUrl();
		if(!image.isEmpty()) {
	        Date date = new Date();
	        String storageFileName = date.getTime() + "_" + image.getOriginalFilename();
        
	         try{
	            String uploadDir = "src/main/resources/static/img/userImage/";
	            Path uploadPath = Paths.get(uploadDir);
	
	            if(!Files.exists(uploadPath))
	            {
	                Files.createDirectories(uploadPath);
	            }
	            try(InputStream inputStream = image.getInputStream()){
	                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
	                        StandardCopyOption.REPLACE_EXISTING);
	            }
	        }
	        catch (Exception ex)
	        {
	            System.out.println("Exception: "+ ex.getMessage());
	        }
	        user.setImageUrl(storageFileName);
		}
		
		if(!userDto.getUsername().isEmpty() && userRepo.findByUsername(userDto.getUsername()) == null ) {
			user.setUsername(userDto.getUsername());
		}
		if(!userDto.getEmail().isEmpty() && userRepo.findByEmail(userDto.getEmail()) == null) {
			user.setEmail(userDto.getEmail());
		}
		if(!userDto.getMobile().isEmpty() && userRepo.findByMobile(userDto.getMobile()) == null) {
			user.setMobile(userDto.getMobile());
		}
		
	    Company company = user.getCompany();
	    
        if(!userDto.getCompanyname().isEmpty()) {
        	company.setName(userDto.getCompanyname());
        }
        company.setUser(user);
        
		userRepo.save(user);
		companyRepo.save(company);
		
		session.setAttribute("message", "User Updated Successfully");
		
		return "redirect:/a2zbilling/admin/viewAdminProfile";
	}

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
		
		//for Alert if product quantity is less then min quantity
		List<Product> products = productRepo.showAllActiveProduct(userId);
		List<Product> minStockProducts = new ArrayList<Product>();
		
		  for(Product product : products) { Stock stock = product.getStock();
		  
		  if(stock != null && Integer.parseInt(stock.getQuantity()) <=
		  Integer.parseInt(stock.getMinQuantity())) { minStockProducts.add(product); }
		  }
		
		StringBuilder productNamesBuilder = new StringBuilder();
		boolean isFirst = true;
		for (Product product : minStockProducts) {
		    if (!isFirst) {
		        productNamesBuilder.append(", ");
		    }
		    productNamesBuilder.append("\"").append(product.getName()).append(" :- \"").append(product.getStock().getQuantity()).append("\"");
		    isFirst = false;
		}
		String productNamesString = productNamesBuilder.toString();

		model.addAttribute("productNamesString", productNamesString);
		model.addAttribute("minStockProducts", minStockProducts);
		
		List<Sales> sales = salesRepo.showAllActiveSales(userId);
		int salesRecordCount  = sales.size();
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
		if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
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
	public String supplierAddingProcess(@ModelAttribute Supplier supplier,HttpSession session, HttpServletRequest request) throws URISyntaxException {
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
	public String addUnit(@ModelAttribute Unit unit, HttpSession session) {
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
		
		
		
		return "redirect:/a2zbilling/admin/";
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
		if(chargesFound == null)
		{
			user.getCharges().add(charges);
			charges.setUser(user);
			chargesRepo.save(charges);
			userRepo.save(user);
			session.setAttribute("message", "Charges Added Successfully !!");
		}
		else {
			chargesFound.setName(charges.getName());
			chargesFound.setPrice(charges.getPrice());
			
			chargesRepo.save(chargesFound);
			session.setAttribute("message", "Charges Updated Successfully !!");
		}

		return "redirect:/a2zbilling/admin/";
	}

	@PostMapping("/category/add")
	public String addingCategory(@ModelAttribute Category category, HttpSession session, HttpServletRequest request) throws URISyntaxException {

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
		//return "redirect:/a2zbilling/admin/product/add";
	}

	@GetMapping("/category/list")
	public String listOfCategories(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		
		Pageable pageable =  PageRequest.of(page,size);
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
	//Created by Younus - to update transections
	@GetMapping("/purchasebill/transection")
	public String purchaseBillList(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
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

		Pageable pageable =  PageRequest.of(page,size);
		Page<PartiesTransaction> partiesTransactions=partiesTransectionRepo.showAllActivePartiesTransection(userId, pageable);
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
		if(purchaseBillString != null && !purchaseBillString.isEmpty()) 
		{
			String newPurchaseBillNo = purchaseBillString.substring(0, 5);
			int no =Integer.parseInt(purchaseBillString.substring(5, purchaseBillString.length()));
			no += 1;
			newPurchaseBillNo += no;
			model.addAttribute("newPurchaseBillNo", newPurchaseBillNo);
		}
		else {
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
		
		String quantity=partiesTransaction.getQuantity();
		int[] quantityArray = Arrays.stream(quantity.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
		
		String taxInPercentage=partiesTransaction.getTaxInPercentage();
		int[] taxInPercentageArray = Arrays.stream(taxInPercentage.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
		user.getPartiesTransactions().add(partiesTransaction);
		partiesTransaction.setUser(user);
		
		partiesTransectionRepo.save(partiesTransaction);
		userRepo.save(user);
		partiesTransaction.setStatus("Active");
		
		List<Product> products=partiesTransaction.getProducts();
		int i=0;
		for(Product product : products) {
			//Stock stock =product.getStock();
			if(product.getStock() != null)
			{
				int id = product.getStock().getId();
				Stock stocks = stockRepo.findById(id).get();
				stocks.setTaxInPercentage(""+taxInPercentageArray[i]);
				int oldQty = Integer.parseInt(stocks.getQuantity());
				
				int newQty = quantityArray[i];
				String addQty = String.valueOf(oldQty + newQty);
				
				stocks.setQuantity(addQty);
				productRepo.save(product);
				stockRepo.save(stocks);
				
			}
			else {
				Stock stock=new Stock();
				stock.setTaxInPercentage(""+taxInPercentageArray[i]);
				stock.setQuantity(""+quantityArray[i]);
				stock.setMinQuantity("10");
				
				
				stockRepo.save(stock);
				
				product.setStock(stock);
				productRepo.save(product);
			}
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
		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier(userId);
		model.addAttribute("suppliers", suppliers);

		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.showAllActiveUnit(userId);
		model.addAttribute("units", units);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

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
	
	@GetMapping("/purchasereturn/transection")
	public String purchaseReturnList(Model model) {
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

		return "admin/purchasereturn_transection";
		
	}
	@GetMapping("/purchasereturn/add")
	public String addPurchaseReturn(Model model) {
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
	
	@GetMapping("/purchasebill/delete/{id}")
	public String deletePurchasebillById(@PathVariable("id") int id) {

		PartiesTransaction partiesTransaction=partiesTransectionRepo.findById(id).get();
		partiesTransaction.setStatus("InActive");
		partiesTransectionRepo.save(partiesTransaction);
		
		return "redirect:/a2zbilling/admin/purchasebill/transection";
	}
	
	@GetMapping("/sales/list")
	public String salesList(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
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
		
		Pageable pageable =  PageRequest.of(page,size);
		Page<Sales> sales = salesRepo.showAllActiveSales(userId,pageable);
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

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products", products);
		
		String salesBillString = salesRepo.maxSalesBillNo(userId);
		if( salesBillString != null && !salesBillString.isEmpty()) 
		{
			String newSaleBillNo = salesBillString.substring(0, 5);
			int no =Integer.parseInt(salesBillString.substring(5, salesBillString.length()));
			no += 1;
			newSaleBillNo += no;
			model.addAttribute("newSaleBillNo", newSaleBillNo);
		}
		else {
			
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
		
		String quantity = sales.getQuantity();
		int[] quantityArray = Arrays.stream(quantity.split(","))
		                            .mapToInt(Integer::parseInt)
		                            .toArray();
		
		sales.setStatus("Active");
		if (sales.getSignatureImage() == null) {
			sales.setSignatureImage("");
		}
		user.getSales().add(sales);
		Customer customer = sales.getCustomer();
		List<Product> products = sales.getProducts();
		sales.setUser(user);
		
		salesRepo.save(sales);
		userRepo.save(user);
		List<Charges> charges = sales.getCharges();
		for (Charges charge : charges) {
			charge.getSales().add(sales);
			chargesRepo.save(charge);
		}
		
		customer.getSales().add(sales);
		List<Product> customerProduct = customer.getProducts();
		int i=0;
		for (Product product : products) {
			Stock stock = product.getStock();
			
			int num = Integer.parseInt(stock.getQuantity()) - quantityArray[i];
			stock.setQuantity(String.valueOf(num));
			product.setStock(stock);
			
			customerProduct.add(product);
			product.getCustomer().add(customer);
			productRepo.save(product);
		}
		customer.setProducts(customerProduct);
		customerRepo.save(customer);

		for (Product product : products) {
			product.getSales().add(sales);
			productRepo.save(product);
		}
		session.setAttribute("message", "Sales Bill Generated Successfully");
		
		String referer = request.getHeader("referer");
		java.net.URI uri = new java.net.URI(referer);
        String path = uri.getPath();
        String query = uri.getQuery();
        String endpoint = path + (query != null ? "?" + query : "");
        return "redirect:" + endpoint;
	}
	
	@GetMapping("/sales/update")
	public String updatesales(Model model){

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
	public String deleteSales(@PathVariable("id") int id) {

		Sales sales = salesRepo.findById(id).get();

		sales.setStatus("InActive");

		salesRepo.save(sales);
		return "redirect:/a2zbilling/admin/sales/list";
	}

	//create by Mahesh
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

		List<Customer> customers = customerRepo.showAllCustomerBYActive(userId);
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.showAllActiveProduct(userId);
		model.addAttribute("products",products);
		
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
		Sales sales = salesRepo.findById(sale.getId()).get();
		
		sales.setDate("");
		sales.setDate(sale.getDate());
		sales.setQuantity(sale.getQuantity());
		sales.setTaxInAmount(sale.getTaxInAmount());
		sales.setTaxInPercentage(sale.getTaxInPercentage());
		sales.setDiscountInAmount(sale.getDiscountInAmount());
		sales.setDiscountInPercentage(sale.getDiscountInPercentage());
		sales.setPaymentMode(sale.getPaymentMode());
		sales.setAmountPaid(sale.getAmountPaid());
		sales.setDueAmount(sale.getDueAmount());
		sales.setNetPayment(sale.getNetPayment());
		sales.setTotalAmount(sale.getTotalAmount());
		sales.setSignatureImage(sale.getSignatureImage());
		
//		new products and customer
		Customer customer = sale.getCustomer();
		List<Product> products = sale.getProducts();
		
//		old products and customer
		List<Product> SalesProduct = sales.getProducts();
		Customer SalesCustomer = sales.getCustomer();
		
		sales.setProducts(products);
		sales.setCustomer(customer);
		
		salesRepo.save(sales);
		return "redirect:/a2zbilling/admin/sales/list";
	}
	
	//Created by Younus
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
	
	//Created by Younus
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
		
		List<Brand> brands=brandRepo.showAllActiveBrand(userId);
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
	
	//Created by Younus
	@PostMapping("/managestock")
	public String manageStockProcess(@ModelAttribute Stock stock, HttpSession session, Model model, HttpServletRequest request) throws URISyntaxException {
		
		Product product = stock.getProduct();
		
		if(product.getStock() != null)
		{
			int id = product.getStock().getId();
			Stock stocks = stockRepo.findById(id).get();
			
			stocks.setMinQuantity(stock.getMinQuantity());
			productRepo.save(product);
			stockRepo.save(stocks);
		}
		else {
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
	public String brandList(Model model,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		
		//Pagination Added
		Pageable pageable =  PageRequest.of(page,size);
		Page<Brand> brands = brandRepo.showAllActiveBrand(userId,pageable);
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
	public String brandaddProcess(@ModelAttribute BrandDto brandDto, Model model, HttpServletRequest request)
	        throws URISyntaxException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
	    // Create a new Brand instance
	    Brand brand = new Brand();
	    brand.setName(brandDto.getName());
	    
	    // Get the uploaded logo file from the DTO
	    MultipartFile file = brandDto.getLogo();
	    
	    // Generate a unique file name using the current timestamp and the original file name
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

	//Created by Younus - update brand Process
	@PostMapping("/brand/update")
	public String updateBrandListProcess(@ModelAttribute BrandDto brandDto) throws IOException {
		Brand brand = brandRepo.findById(brandDto.getId()).get();
		brand.setName(brandDto.getName());
		
	    // Get the uploaded logo file from the DTO
	    MultipartFile file = brandDto.getLogo();
	    
	    // Generate a unique file name using the current timestamp and the original file name
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
	
	//Created by Younus -for soft delete (Status - Inactive)
	@GetMapping("/brand/delete/{id}")
	public String deleteBrandById(@PathVariable("id") int id) {

		Brand brand=brandRepo.findById(id).get();
		brand.setStatus("InActive");
		brandRepo.save(brand);
		
		return "redirect:/a2zbilling/admin/brand/list";
	}
	
	//Created by Younus
	@GetMapping("/purchaseReport")
	public String purchaseReport(Model model,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		// To get Parties Name data from db
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);
		
		//Pagination Added
		Pageable pageable =  PageRequest.of(page,size);
		Page<PartiesTransaction> partiesTransactions=partiesTransectionRepo.showAllActivePartiesTransection(userId,pageable);
		model.addAttribute("partiesTransactions", partiesTransactions);
		model.addAttribute("currentPage", page);
				
		Company company = companyRepo.getCompanyByUserId(userId);
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
		
		return "admin/purchase_Report";
	}
	
	//Created by Younus
	@GetMapping("/salesReport")
	public String salesReport(Model model,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		
		int userId=user.getId();
		// To get sale data from db
		
		//Pagination Added
		Pageable pageable =  PageRequest.of(page,size);
		Page<Sales> sales = salesRepo.showAllActiveSales(userId,pageable);
		model.addAttribute("sales", sales);
		model.addAttribute("currentPage", page);
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
		
		return "admin/sales_Report";
	}
  
	//Created by Younus 
		@GetMapping("/purchaseTaxReport")
		public String purchaseTaxReport(Model model,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userRepo.findByUsername(auth.getName());
			
			int userId=user.getId();
			
			// To get Parties Name data from db
			List<Parties> parties = partiesRepo.showAllActiveParties(userId);
			model.addAttribute("parties", parties);
			
			//Pagination Added
			Pageable pageable =  PageRequest.of(page,size);
			Page<PartiesTransaction> partiesTransactions=partiesTransectionRepo.showAllActivePartiesTransection(userId,pageable);
			model.addAttribute("partiesTransactions", partiesTransactions);
			model.addAttribute("currentPage", page);		
			
			Company company = companyRepo.getCompanyByUserId(user.getId());
			String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
			model.addAttribute("imagePath", imgpath);

			String image = company.getLogo();
			String companyLogo = "/img/companylogo/" + image;
			model.addAttribute("companyLogo", companyLogo);
			
			return "admin/purchase_Tax_Report";
		}

		//Created by Younus salesTaxReport
				@GetMapping("/salesTaxReport")
				public String salesTaxReport(Model model,@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {
					Authentication auth = SecurityContextHolder.getContext().getAuthentication();
					User user = userRepo.findByUsername(auth.getName());
					
					// To get sale data from db
					int Userid=user.getId();
					
					//Pagination Added
					Pageable pageable =  PageRequest.of(page,size);
					Page<Sales> sales = salesRepo.showAllActiveSales(Userid,pageable);
					model.addAttribute("sales", sales);
					model.addAttribute("currentPage", page);
				
					Company company = companyRepo.getCompanyByUserId(user.getId());
					String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
					model.addAttribute("imagePath", imgpath);

					String image = company.getLogo();
					String companyLogo = "/img/companylogo/" + image;
					model.addAttribute("companyLogo", companyLogo);
					
					return "admin/sales_Tax_Report";
				}
}
