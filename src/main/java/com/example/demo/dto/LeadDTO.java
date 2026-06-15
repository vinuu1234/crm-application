package com.example.demo.dto;

import java.time.LocalDateTime;

import com.example.demo.entity.FollowUpStage;
import com.example.demo.entity.LeadSource;
import com.example.demo.entity.LeadStatus;

import lombok.Data;

@Data
public class LeadDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private String address;
    private LeadSource source;
    private LeadStatus status;
    private FollowUpStage currentStage;
    private Long assignedToId;
    private String assignedToName;
    private Long assignedById;
    private String assignedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}