package com.billing.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
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
import com.billing.model.Brand;
import com.billing.model.Category;
import com.billing.model.Color;
import com.billing.model.Company;
import com.billing.model.Customer;
import com.billing.model.Parties;
import com.billing.model.Product;
import com.billing.model.Size;
import com.billing.model.Stock;
import com.billing.model.Supplier;
import com.billing.model.User;
import com.billing.repositories.BrandRepository;
import com.billing.repositories.CategoryRepository;
import com.billing.repositories.ColorRepository;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.CustomerRepository;
import com.billing.repositories.GSTRepository;
import com.billing.repositories.PartiesRepository;
import com.billing.repositories.ProductRepository;
import com.billing.repositories.SizeRepository;
import com.billing.repositories.StockRepository;
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
public class ProductController {

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
	
	@ModelAttribute("product")
	public Product product(){
		
		return new Product();
	}
	
	@GetMapping("/product/add")
	public String addProductByAdmin(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		int userId = user.getId();
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		List<Customer> customerList = customerRepo.findAll();
		model.addAttribute("customers", customerList);

		List<Category> categoryList = categoryRepo.findByActiveCategory(userId);
		model.addAttribute("categories", categoryList);

		List<Color> colors = colorRepo.findAll();
		model.addAttribute("colors", colors);

		List<Size> sizes = sizeRepo.showAllSize(userId);
		model.addAttribute("sizes", sizes);

		List<Brand> brands = brandRepo.showAllActiveBrand(userId);
		model.addAttribute("brands", brands);

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

		model.addAttribute("companyName", companyName);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/add_product_form";

	}

	@PostMapping("/product/add")
	public String processProductAdding(@ModelAttribute Product product,
			@RequestParam("productSize") String productSizeValue, @RequestParam("productColor") String colorName,
			HttpSession session) throws IOException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());

		user.getProducts().add(product);
		userRepo.save(user);

		product.setUser(user);
		/*
		 * String supplierName=product.getSupplier().getName();
		 * System.out.println(supplierName);
		 */
		// adding size

		Size sizeOccured = sizeRepo.findBySizeValue(productSizeValue);

		if (sizeOccured != null) {

			product.setSize(sizeOccured);
		} else {

			Size s = new Size();
			s.setSizeValue(productSizeValue);
			s.setUser(user);
			sizeRepo.save(s);
			user.getSizes().add(s);
			userRepo.save(user);
			product.setSize(s);
			user.getSizes().add(s);
			userRepo.save(user);
		}

		// adding color
		Color colorFound = colorRepo.findByName(colorName);

		if (colorFound != null) {

			product.setColor(colorFound);
		} else {
			Color co = new Color();
			co.setName(colorName);
			colorRepo.save(co);
			product.setColor(co);
		}

		/*
		 * // add brand into product Brand brandFound = brandRepo.findByName(brandName);
		 * 
		 * if (brandFound != null) {
		 * 
		 * product.setBrand(brandFound); } else {
		 * 
		 * Brand b = new Brand(); b.setName(brandName); b.setLogo("default.png");
		 * brandRepo.save(b); product.setBrand(b); }
		 */

		Brand brand = product.getBrand();
		brand.getProducts().add(product);
		brandRepo.save(brand);
		product.setBrand(brand);

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

	// update form handler
	@GetMapping("/product/edit/{id}")
	public String productEditForm(@PathVariable("id") String id, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());
		String companyName = company.getName();

		List<Brand> brands = brandRepo.showAllActiveBrand(userId);
		model.addAttribute("brands", brands);

		model.addAttribute("companyName", companyName);
		Optional<Product> Founded = productRepo.findById(Integer.parseInt(id));
		Product product = Founded.get();
		model.addAttribute("product", product);
		List<Category> categories = categoryRepo.findByActiveCategory(userId);
		model.addAttribute("categories", categories);
		List<Color> colors = colorRepo.findAll();
		model.addAttribute("colors", colors);

		int userid = user.getId();
		List<Parties> parties = partiesRepo.showAllActiveParties(userId);
		model.addAttribute("parties", parties);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
		if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
			String Userimage = user.getImageUrl();
			imgpath = StringUtils.ImagePaths.userImageUrl + Userimage;
		}
		model.addAttribute("imagePath", imgpath);

		return "admin/edit_product";

	}

	@PostMapping("/product/edit")
	public String productUpdateProcess(@ModelAttribute("product") Product product,
			@RequestParam("productColor") String colorName, HttpSession session) throws IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Optional<Product> found = productRepo.findById(product.getId());
		Product productFound = found.get();

		productFound.setName(product.getName());
		productFound.setAddedDate(product.getAddedDate());
		productFound.setPrice(product.getPrice());
		productFound.setParties(product.getParties());
		productFound.setAbout(product.getAbout());

		String productSizeValue = product.getSize().getSizeValue(); // Assuming you have a 'getSize()' method in your
																	// Product class
		Size sizeOccured = sizeRepo.findBySizeValue(productSizeValue);
		if (sizeOccured != null) {
			productFound.setSize(sizeOccured);
			sizeOccured.setSizeValue(productSizeValue);
			sizeRepo.save(sizeOccured);
		} else {

			Size s = new Size();
			s.setSizeValue(productSizeValue);
			s.setUser(user);
			sizeRepo.save(s);
			user.getSizes().add(s);

			userRepo.save(user);
			productFound.setSize(s);
		}

		Color colorFound = colorRepo.findByName(colorName);
		if (colorFound != null) {
			colorFound.getProducts().add(product);
			colorRepo.save(colorFound);

			productFound.setColor(colorFound);
		} else {
			Color color = new Color();
			color.setName(colorName);

			colorRepo.save(color);
			productFound.setColor(color);
		}

		Brand brand = product.getBrand();
		if (brand != null) {
			brand.getProducts().add(product);
			brandRepo.save(brand);
			productFound.setBrand(brand);
		}

		Category cat = categoryRepo.findByCategoryName(product.getCategory().getCategoryName());
		if (cat != null) {
			productFound.setCategory(cat);
		}

		productFound.setStatus("Active");

		productRepo.save(productFound);

		return "redirect:/a2zbilling/admin/product/list";
	}

	// delete product handler
	@GetMapping("/product/delete/{id}")
	public String deleteProductById(@PathVariable("id") String id) {

		Optional<Product> productGet = productRepo.findById(Integer.parseInt(id));
		Product product = productGet.get();

		product.setStatus("InActive");

		productRepo.save(product);

		return "redirect:/a2zbilling/admin/product/list";

	}

	// showing all products
	@GetMapping("/product/list")
	public String showAllProduct(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepo.findByUsername(auth.getName());
		int userId = user.getId();

		Page<Product> productPage = productService.getAvailableProducts(page, size);
		model.addAttribute("productPage", productPage);
		model.addAttribute("currentPage", page);

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

		model.addAttribute("companyName", companyName);

		String image = company.getLogo();
		String companyLogo = "/img/companylogo/" + image;
		model.addAttribute("companyLogo", companyLogo);

		return "admin/product_list";
	}
}
