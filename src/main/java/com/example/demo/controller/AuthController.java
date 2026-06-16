package com.example.demo.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    
    @Autowired
    private EmailService emailService;
    
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken authToken = 
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            
            Authentication authentication = authenticationManager.authenticate(authToken);
            
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            System.out.print(loginRequest.getPassword());
            User user = (User) authentication.getPrincipal();
            UserDTO userDTO = userService.getUserById(user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", userDTO);
            response.put("role", user.getRole());
            response.put("sessionId", session.getId());
            response.put(
                    "passwordChangeRequired",
                    user.isPasswordChangeRequired()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "Logout successful");
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                User user = (User) auth.getPrincipal();
                UserDTO userDTO = userService.getUserById(user.getId());
                
                Map<String, Object> response = new HashMap<>();
                response.put("authenticated", true);
                response.put("user", userDTO);
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            }
        }
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        
        User user = (User) authentication.getPrincipal();
        UserDTO userDTO = userService.getUserById(user.getId());
        
        return ResponseEntity.ok(Map.of(
            "user", userDTO,
            "role", user.getRole()
        ));
    }
    
    @GetMapping("/session-expired")
    public ResponseEntity<?> sessionExpired() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Session expired");
        response.put("message", "Your session has expired. Please login again.");
        return ResponseEntity.status(401).body(response);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            Authentication authentication,
            @RequestBody ChangePasswordRequest request) {

        userService.changePassword(
                authentication.getName(),
                request);

        return ResponseEntity.ok(
                Map.of("message",
                       "Password changed successfully"));
    }
    
    @GetMapping("/test-mail")
    public String testMail() {

        emailService.sendWelcomeEmail(
                "yourpersonalemail@gmail.com",
                "Temp@123"
        );

        return "Mail sent";
    }
}