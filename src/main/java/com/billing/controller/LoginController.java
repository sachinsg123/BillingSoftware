package com.billing.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.billing.model.Company;
import com.billing.model.User;
import com.billing.model.UserDto;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.UserRepository;

@Controller
@RequestMapping("/auth")
public class LoginController{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private CompanyRepository companyRepo;
	
	@GetMapping("/login-user")
	public String loginPage(){
		
		return "/users/loginform";
		
	}
	
	//created by Mahesh
	@PostMapping("/registration")
	public String addingProcessUser(@ModelAttribute UserDto userDto) {
		
		MultipartFile image = userDto.getImageUrl();
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
        
         String password = userDto.getPassword();
         String encodedPass = passwordEncoder.encode(password);

 		System.out.println(encodedPass);
 		
         User user = new User();
         
         user.setUsername(userDto.getUsername());
         user.setEmail(userDto.getEmail());
         user.setMobile(userDto.getMobile());
         user.setImageUrl(storageFileName);
         user.setPassword(encodedPass);
         user.setStatus("ACTIVE");
         user.setRole("ADMIN");
         
         
         
         if(companyRepo.findByName(userDto.getCompanyname()) != null) {
        	 System.out.println("Company Name Already Present");
        	 return "redirect:/auth/login-user";
         }
         Company company = new Company();
         
         company.setName(userDto.getCompanyname());
         company.setUser(user);
         
         
         userrepo.save(user);
         
         companyRepo.save(company);
         
         System.out.println("User Added Successfully");
		return "redirect:/auth/login-user";
	}
	
	
	
			
	}


