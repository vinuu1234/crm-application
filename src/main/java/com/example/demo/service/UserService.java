package com.example.demo.service;


import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.dto.CreateUserRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.EmailService;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserDTO createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        user.setDepartment(request.getDepartment());
        user.setPhone(request.getPhone());

        String tempPassword = generateTemporaryPassword();

        user.setPassword(
                passwordEncoder.encode(tempPassword)
        );

        user.setPasswordChangeRequired(true);
        user.setActive(true);

  
        User savedUser = userRepository.save(user);

        emailService.sendWelcomeEmail(
                savedUser.getEmail(),
                tempPassword
        );

        System.out.println(
                "Temporary Password for "
                        + savedUser.getEmail()
                        + " : "
                        + tempPassword
        );

        System.out.println(
                "Temporary Password for "
                        + savedUser.getEmail()
                        + " : "
                        + tempPassword
        );

        return getUserById(savedUser.getId());
    }
    
    
    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user =
                userRepository.findByEmail(email)
                        .orElseThrow();

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Old password incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()));

        user.setPasswordChangeRequired(false);

        userRepository.save(user);
    }
    private String generateTemporaryPassword() {

        String chars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";

        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            password.append(
                    chars.charAt(
                            random.nextInt(chars.length())
                    )
            );
        }

        return password.toString();
    }
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToDTO(user);
    }
    
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<UserDTO> getTeamMembers(Long managerId) {
        return userRepository.findByManagerId(managerId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public UserDTO updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setName(userDetails.getName());
        user.setDepartment(userDetails.getDepartment());
        user.setPhone(userDetails.getPhone());
        user.setRole(userDetails.getRole());
        
        if (userDetails.getManager() != null) {
            user.setManager(userDetails.getManager());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setPhone(user.getPhone());
        dto.setActive(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        
        if (user.getManager() != null) {
            dto.setManagerId(user.getManager().getId());
            dto.setManagerName(user.getManager().getName());
        }
        
        return dto;
    }
   
}