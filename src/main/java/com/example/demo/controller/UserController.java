package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CreateUserRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3003"}, allowCredentials = "true")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Only Admin can see all users
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/team")
    public ResponseEntity<?> getTeamMembers(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getRole() == Role.ADMIN) {
            List<UserDTO> allUsers = userService.getAllUsers();
            return ResponseEntity.ok(allUsers);
        } else if (currentUser.getRole() == Role.MANAGER) {
            List<UserDTO> teamMembers = userService.getTeamMembers(currentUser.getId());
            return ResponseEntity.ok(teamMembers);
        } else {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
    }
    
    @GetMapping("/employees")
    public ResponseEntity<?> getEmployees(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.MANAGER) {
            List<UserDTO> employees = userService.getUsersByRole(Role.EMPLOYEE);
            return ResponseEntity.ok(employees);
        }
        
        return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
    }
    
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Only Admin can create users"));
        }

        try {
            UserDTO userDTO = userService.createUser(request);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User created successfully",
                    "user", userDTO
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Only Admin can update users
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admin can update users"));
        }
        
        try {
            UserDTO updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Only Admin can delete users
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admin can delete users"));
        }
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}