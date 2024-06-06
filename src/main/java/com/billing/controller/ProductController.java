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
	
	
	@GetMapping("/product/add")
	public String addProductByAdmin(Model model) {

		List<Supplier> suppliers = supplierRepo.showAllActiveSupplier();
		model.addAttribute("suppliers", suppliers);

		List<Customer> customerList = customerRepo.findAll();
		model.addAttribute("customers", customerList);

		List<Category> categoryList = categoryRepo.findAll();
		model.addAttribute("categories", categoryList);

		List<Color> colors = colorRepo.findAll();
		model.addAttribute("colors", colors);
		
		List<Brand> brands=brandRepo.showAllActiveBrand();
		model.addAttribute("brands", brands);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		User user = userRepo.findByUsername(auth.getName());
		String username = auth.getName();
		String email = user.getEmail();
		model.addAttribute("username", username);
		model.addAttribute("email", email);

		Company company = companyRepo.getCompanyByUserId(user.getId());

		String companyName = company.getName();

		String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";

		if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
	    {
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
			@RequestParam("imageFile") MultipartFile imageFile, @RequestParam("productSize") String productSizeValue,
			@RequestParam("productColor") String colorName, @RequestParam("productBrand") String brandName,
			HttpSession session) throws IOException {

		if (imageFile.getOriginalFilename().isEmpty()) {

			// imageFile.getOriginalFilename();
			product.setImageUrl("default.png");
		} else {
			// String uploadDir = "./static/productimages";
			String fileName = imageFile.getOriginalFilename();
			// Path filePath = Paths.get(StringUtils.ImagePaths.productImageUrl).getFile();
			File filePath = new ClassPathResource("/static/img/productimages/").getFile();
			Path path = Paths.get(filePath.getAbsolutePath() + File.separator + fileName);
			Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			System.out.println(fileName);
			System.out.println(path);
			System.out.println(filePath);
			product.setImageUrl(fileName);
		}

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
	// update form handler
		@GetMapping("/product/edit/{id}")
		public String productEditForm(@PathVariable("id") String id, Model model) {

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userRepo.findByUsername(auth.getName());
			String username = auth.getName();
			String email = user.getEmail();
			model.addAttribute("username", username);
			model.addAttribute("email", email);

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

			String image = company.getLogo();
			String companyLogo = "/img/companylogo/" + image;
			model.addAttribute("companyLogo", companyLogo);


			String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
			if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
		    {
		    	String Userimage = user.getImageUrl();
		    	imgpath = StringUtils.ImagePaths.userImageUrl + Userimage;
		    }
			model.addAttribute("imagePath", imgpath);

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
//				   String uploadDir = "./static/productImages";
				String fileName = imageFile.getOriginalFilename();
//				   Path filePath = Paths.get(StringUtils.ImagePaths.productImageUrl).getFile();
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
//				sizeRepo.save(s);
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
//				colorRepo.save(co);
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
//				brandRepo.save(b);
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
		
		//delete product handler 
		@GetMapping("/product/delete/{id}")
		public String deleteProductById(@PathVariable("id") String id){
			 
			Optional<Product> productGet= productRepo.findById(Integer.parseInt(id));
			Product product = productGet.get();
			
			product.setStatus("deleted");
			
			productRepo.save(product);
			
			return "redirect:/a2zbilling/admin/product/list";
			
		}
		
		// showing all products
		@GetMapping("/product/list")
		public String showAllProduct(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="10") int size) {

			Page<Product> productPage = productService.getAvailableProducts(page, size);
//			model.addAttribute("products", allProducts);
			model.addAttribute("productPage", productPage);
			model.addAttribute("currentPage", page);

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userRepo.findByUsername(auth.getName());
			String username = auth.getName();
			String email = user.getEmail();
			model.addAttribute("username", username);
			model.addAttribute("email", email);
			
			Company company = companyRepo.getCompanyByUserId(user.getId());
			String companyName = company.getName();

			String imgpath = StringUtils.ImagePaths.adminImageUrl + "admin.jpg";
			if(user.getImageUrl() != null && !user.getImageUrl().isEmpty())
		    {
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
