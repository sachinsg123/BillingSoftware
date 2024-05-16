package com.billing.controller;

import java.io.InputStream;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Random;

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

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Controller
@RequestMapping("/auth")
public class LoginController{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userrepo;
	
	@Autowired
	private CompanyRepository companyRepo;
	
	@Autowired
    private JavaMailSender javaMailSender;
	
	
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
	
	public String generateOTP(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return otp.toString();
    }
	
	public void sendOTPEmail(String to, String subject, String body) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        
        System.out.println(body);
        
        javaMailSender.send(message);
    }
	
	//created by Mahesh
	@GetMapping("/sendOTPEmail")
	public void sendOTPEmail() {
        String otp = generateOTP(6); // Generate a 6-digit OTP
        String subject = "Your OTP for Verification";
        String body = "Your OTP is: " + otp + ". Please use this OTP to verify your email.";
        
        System.out.println(otp);
        
        
        try {
            sendOTPEmail("maheshmisal2018@gmail.com", subject, body);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }
			
}


