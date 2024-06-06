package com.billing.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.billing.model.Brand;
import com.billing.model.BrandDto;
import com.billing.model.Category;
import com.billing.model.Company;
import com.billing.model.CompanyDto;
import com.billing.model.Customer;
import com.billing.model.GSTRate;
import com.billing.model.Parties;
import com.billing.model.PartiesTransaction;
import com.billing.model.Product;
import com.billing.model.Sales;
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
import com.billing.repositories.PartiesRepository;
import com.billing.repositories.PartiesTransectionRepository;
import com.billing.repositories.ProductRepository;
import com.billing.repositories.SalesRepository;
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
	private BrandRepository brandRepo;

	@Autowired
	private PartiesRepository partiesRepo;

	@Autowired
	private PartiesTransectionRepository partiesTransectionRepo;

  @Autowired
	private SalesRepository salesRepo;
	
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
		
		
	    System.out.println("Company Name "+userDto.getCompanyname() + " " +user.getCompany().getName());
	    Company company = user.getCompany();
	    
        if(!userDto.getCompanyname().isEmpty()) {
        	company.setName(userDto.getCompanyname());
        }
        company.setUser(user);
        
		userRepo.save(user);
		companyRepo.save(company);
		
		return "redirect:/a2zbilling/admin/viewAdminProfile";
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

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		String sign = company.getSignature();
		String companySign = "/img/companysignature/" + sign;
		model.addAttribute("companySign", companySign);

		long customercount = customerService.getCustomerCount();
		model.addAttribute("customercount", customercount);

		long suppliercount = supplierService.getSupplierCount();
		model.addAttribute("suppliercount", suppliercount);

		return "home";

	}
	
	@PostMapping("/supplier/add")
	public String supplierAddingProcess(@ModelAttribute Supplier supplier, HttpSession session,
			HttpServletRequest request) throws URISyntaxException {

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

		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier();
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
			session.setAttribute("message", "Category added successfully!");
		} else if (category.getCategoryName().equals(cFound.getCategoryName())) {
			session.setAttribute("message", "Category already exists!");
		}

		return "redirect:/a2zbilling/admin/product/add";
	}

	@GetMapping("/category/list")
	public String listOfCategories(Model model) {

		List<Category> categories = categoryRepo.findByActiveCategory();
		model.addAttribute("categories", categories);

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
	
	@GetMapping("/purchasebill/transection")
	public String purchaseBillList(Model model) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		List<PartiesTransaction> parties = partiesTransectionRepo.findAll();
		model.addAttribute("parties", parties);

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

		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.findAll();
		model.addAttribute("units", units);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.findAll();
		model.addAttribute("sizes", sizes);

		// To get product Name data from db
		List<Product> products = productRepo.findAll();
		model.addAttribute("products", products);

		// To get Parties Name data from db
		List<Parties> parties = partiesRepo.showAllActiveParties();
		model.addAttribute("parties", parties);

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

		partiesTransaction.setStatus("Active");
		partiesTransectionRepo.save(partiesTransaction);

		return "redirect:/a2zbilling/admin/purchasebill/transection";

	}
	@GetMapping("/purchasebill/update")
	public String updatePurchaseBill(Model model) {

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

		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier();
		model.addAttribute("suppliers", suppliers);

		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.findAll();
		model.addAttribute("units", units);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.findAll();
		model.addAttribute("sizes", sizes);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
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
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier();
		model.addAttribute("suppliers", suppliers);

		// to render unit list on Purchase bill page
		List<Unit> units = unitRepo.findAll();
		model.addAttribute("units", units);

		// to render list on Purchase bill page
		List<Size> sizes = sizeRepo.findAll();
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
	
	@GetMapping("/sales/list")
	public String salesList(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		List<Sales> sales = salesRepo.showAllActiveSales();
		model.addAttribute("sales", sales);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String image = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + image;
		}
		model.addAttribute("imagePath", imgpath);

		List<Customer> customers = customerRepo.findAll();
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.findAll();
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
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		List<Customer> customers = customerRepo.showAllCustomerBYActive();
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.findAll();
		model.addAttribute("products", products);

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
	public String salesAddingProcess(@ModelAttribute Sales sales,HttpSession session, HttpServletRequest request) throws URISyntaxException {
		
		sales.setStatus("Active");
  if (sales.getSignatureImage() == null) {
			sales.setSignatureImage("");
		}

		Customer customer = sales.getCustomer();
		List<Product> products = sales.getProducts();

		salesRepo.save(sales);
		customer.getSales().add(sales);
		List<Product> customerProduct = customer.getProducts();
		for (Product product : products) {
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
		
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		Company company = companyRepo.getCompanyByUserId(user.getId());

		List<Customer> customers = customerRepo.showAllCustomerBYActive();
		model.addAttribute("customers", customers);

		List<Product> products = productRepo.findAll();

		model.addAttribute("products",products);
		
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
	
	@GetMapping("/Item/add")
	public String addItem(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		// To get product Name data from db
		List<Product> products = productRepo.findAll();
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
	
	@GetMapping("/managestock")
	public String manageStock(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();
		model.addAttribute("companyName", companyName);

		
		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier();
		model.addAttribute("suppliers", suppliers);

		
		List<Product> products = productRepo.findAll();
		model.addAttribute("products", products);

		
		List<Category> categorys = categoryRepo.findAll();
		model.addAttribute("categorys", categorys);
		
		List<Brand> brands=brandRepo.showAllActiveBrand();
		model.addAttribute("brands", brands);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		model.addAttribute("imagePath", imgpath);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/manage_stock";

	}

	// Created by Younus - get Brand list
	@GetMapping("/brand/list")
	public String brandList(Model model) {

		List<Brand> brands = brandRepo.showAllActiveBrand();
		model.addAttribute("brands", brands);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
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
	    // Save the brand to the repository
	    brandRepo.save(brand);

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


	//Craeted by Younus - Update Brand
	@GetMapping("/brand/update/{id}")
	public String updateBrandList(@PathVariable int id, Model model) {

		Brand brand = brandRepo.findById(id).get();
		model.addAttribute("brand", brand);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		Company company = companyRepo.getCompanyByUserId(user.getId());
		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
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

}
