package com.billing.controller;

import java.io.InputStream;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Random;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.billing.model.Company;
import com.billing.model.User;
import com.billing.model.UserDto;
import com.billing.repositories.CompanyRepository;
import com.billing.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
	
	private String storedOtp = "";
	
	@GetMapping("/login-user")
	public String loginPage(){
		
		return "/users/loginform";
		
	}
	
	//created by Mahesh
	@GetMapping("/clearSessionAttribute")
	public String clearSession(HttpSession session, HttpServletRequest request) {
		String referer = request.getHeader("referer");
		if (session.getAttribute("message") != null) {

			session.removeAttribute("message");
			if (referer != null && !referer.isEmpty()) {
				return "redirect:" + referer;
			}
			return "redirect:/auth/login-user";
		}
		session.removeAttribute("message");
		return "redirect:/auth/registration";
	}
	
	//created by Mahesh
	@PostMapping("/registration")
	public String addingProcessUser(@ModelAttribute UserDto userDto, HttpSession session) {
		
		MultipartFile image = userDto.getImageUrl();
		String storageFileName = "";
		if(!image.isEmpty()) {
	        Date date = new Date();
	        storageFileName = date.getTime() + "_" + image.getOriginalFilename();
        
        
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
	        
		}
         String password = userDto.getPassword();
         String encodedPass = passwordEncoder.encode(password);

 		System.out.println(encodedPass);
 		
         User user = new User();
         
         user.setUsername(userDto.getUsername());
         if(userrepo.findByEmail(userDto.getEmail()) != null)
         {
        	 session.setAttribute("message", "This Email Already Used");
        	 return "redirect:/auth/login-user";
         }
         
         user.setEmail(userDto.getEmail());
         
         if(userrepo.findByMobile(userDto.getMobile()) != null)
         {
        	 session.setAttribute("message", "This Mobile Number Already Used");
        	 return "redirect:/auth/login-user";
         }
         user.setMobile(userDto.getMobile());
         user.setImageUrl(storageFileName);
         user.setPassword(encodedPass);
         user.setStatus("ACTIVE");
         user.setRole("ADMIN");
         
         
         
         if(companyRepo.findByName(userDto.getCompanyname()) != null) {
        	 session.setAttribute("message", "Company Name Already Present");
        	 return "redirect:/auth/login-user";
         }
         Company company = new Company();
         
         company.setName(userDto.getCompanyname());
         company.setUser(user);
         
         
         userrepo.save(user);
         
         companyRepo.save(company);
         
        session.setAttribute("message", "User Added Successfully");
		return "redirect:/auth/login-user";
	}
	
	@PostMapping("/checkValidOTP")
    public ResponseEntity<String> checkValidOTP(@RequestParam("otp") String otp) {
        if(otp.equals(storedOtp)) {
        	return ResponseEntity.ok("Email Verified Successfully !!");
        }
        return ResponseEntity.ok("Entered OTP is Wrong");
    }
	
	//created by Mahesh
	public String generateOTP(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }
        return otp.toString();
    }
	
	//created by Mahesh
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
	@PostMapping("/sendOTPEmail")
    public ResponseEntity<String> sendOTPEmail1(@RequestParam("email") String email) {
		
		User user = userrepo.findByEmail(email);
		
		if(user != null) return ResponseEntity.ok("Email Present");
		
        String otp = generateOTP(6); // Generate a 6-digit OTP
        storedOtp = otp;
        String subject = "Your OTP for Verification";
        String body = "Your OTP is : " + otp + ". Please use this OTP to verify your email.";

        try {
        	sendOTPEmail(email, subject, body); // Assuming this is the correct method signature
        	
            return ResponseEntity.ok("OTP sent to " + email + " successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
        }
    }
	
	//created by Mahesh
	@PostMapping("/sendOTPForForgotPassword")
    public ResponseEntity<String> sendOTPForForgotPassword(@RequestParam("email") String email) {
		
		User user = userrepo.findByEmail(email);
		
		if(user == null) return ResponseEntity.ok("Email Not Present");
		
        String otp = generateOTP(6); // Generate a 6-digit OTP
        storedOtp = otp;
        String subject = "Your OTP for Verification";
        String body = "Your OTP is : " + otp + ". Please use this OTP to verify your email.";

        try {
        	sendOTPEmail(email, subject, body); // Assuming this is the correct method signature
        	
            return ResponseEntity.ok("OTP sent to " + email + " successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
        }
    }
			
}


