package com.example.demo.dto;


import java.time.LocalDateTime;

import com.example.demo.entity.Role;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String department;
    private String phone;
    private boolean isActive;
    private Long managerId;
    private String managerName;
    private LocalDateTime createdAt;
}