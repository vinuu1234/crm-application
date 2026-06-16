package com.example.demo.dto;

import com.example.demo.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Role role;

    private String department;

    private String phone;

    private Long managerId;
}