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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	
	@PostMapping("/sendOTPForUpdateEmail")
    public ResponseEntity<String> sendOTPForUpdateEmail(@RequestParam("otp") String otp, @RequestParam("email") String email) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userrepo.findByUsername(auth.getName());
        if(otp.equals(storedOtp)) {
        	user.setEmail(email);
        	userrepo.save(user);
        	return ResponseEntity.ok("Email Updated Successfully !!");
        }
        return ResponseEntity.ok("Entered OTP is Wrong");
    }
	
	@PostMapping("/checkValidOTPForForgotPassword")
    public ResponseEntity<String> checkValidOTPForForgotPassword(@RequestParam("otp") String otp) {
        if(otp.equals(storedOtp)) {
        	return ResponseEntity.ok("Email Verified Successfully !!");
        }
        return ResponseEntity.ok("Entered OTP is Wrong");
    }
	
	@PostMapping("/updateUserPassword")
    public String updateUserPassword(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session) {
		
		User user = userrepo.findByEmail(email);
		String encodedPass = passwordEncoder.encode(password);
		
		user.setPassword(encodedPass);
		
		userrepo.save(user);
		
		System.out.println(email);
		System.out.println(password);
		
		session.setAttribute("message", "User Password Updated Successfully");
        return "redirect:/auth/login-user";
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
	public void sendOTPEmail(String to, String subject, String body, String imagePath) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // Set to true for HTML content

        ClassPathResource resource = new ClassPathResource(imagePath);
        helper.addInline("image1", resource);
        javaMailSender.send(message);
        
    }
	
	//created by Mahesh
	@PostMapping("/sendOTPEmail")
    public ResponseEntity<String> sendOTPEmail1(@RequestParam("email") String email) {
		User user = userrepo.findByEmail(email);
		
		if(user != null) return ResponseEntity.ok("Email Present");
		
        String otp = generateOTP(6); // Generate a 6-digit OTP
        System.out.println(otp);
        storedOtp = otp;
        String subject = "Your OTP for Verification : " + otp;
        String body = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                "<img src='cid:image1' style='width: 10%; height: 10%;'>" +
                "<h2 style='color: #2C3E50; text-align: center;'>OTP Verification</h2>" +
                "<p style='font-size: 16px; color: #333;'>Dear User,</p>" +
                "<p style='font-size: 16px; color: #333;'>Your OTP is:</p>" +
                "<div style='font-size: 24px; color: green; text-align: center; margin: 20px 0;'><strong>" + otp + "</strong></div>" +
                "<p style='font-size: 16px; color: #333;'>Please use this OTP to verify your email address. This OTP is valid for the next 10 minutes.</p>" +
                "<p style='font-size: 16px; color: #333;'>If you did not request this OTP, please ignore this email or contact support.</p>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "<a href='https://a2zithub.org/training/a2z/contact' style='padding: 10px 20px; background-color: #3498DB; color: #fff; text-decoration: none; border-radius: 5px;'>Contact Support</a>" +
                "</div>" +
                "<p style='font-size: 14px; color: #777; text-align: center; margin-top: 20px;'>Thank you for using our service!</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        try {
        	sendOTPEmail(email, subject, body,"static/img/favicon/A2Zlogo.jpeg"); // Assuming this is the correct method signature
        	
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
        System.out.println(otp);
        storedOtp = otp;
        String subject = "Your OTP for Verification : " + otp;
        String body = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
                "<div style='display: flex; align-items: center; justify-content: center;'>" +
                "<img src='cid:image1' style='width: 10%; height: 10%; margin-right: 28%;'>" +
                "<h2 style='color: #2C3E50; text-align: left;'>OTP Verification</h2>" +
                "</div>" +
                "<p style='font-size: 16px; color: #333;'>Dear User,</p>" +
                "<p style='font-size: 16px; color: #333;'>Your OTP is:</p>" +
                "<div style='font-size: 24px; color: green; text-align: center; margin: 20px 0;'><strong>" + otp + "</strong></div>" +
                "<p style='font-size: 16px; color: #333;'>Please use this OTP to verify your email address. This OTP is valid for the next 10 minutes.</p>" +
                "<p style='font-size: 16px; color: #333;'>If you did not request this OTP, please ignore this email or contact support.</p>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "<a href='https://a2zithub.org/training/a2z/contact' style='padding: 10px 20px; background-color: #3498DB; color: #fff; text-decoration: none; border-radius: 5px;'>Contact Support</a>" +
                "</div>" +
                "<p style='font-size: 14px; color: #777; text-align: center; margin-top: 20px;'>Thank you for using our service!</p>" +
                "</div>" +
                "</body>" +
                "</html>";



        try {
        	sendOTPEmail(email, subject, body,"static/img/favicon/A2Zlogo.jpeg"); // Assuming this is the correct method signature
        	
            return ResponseEntity.ok("OTP sent to " + email + " successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP");
        }
    }
			
}


