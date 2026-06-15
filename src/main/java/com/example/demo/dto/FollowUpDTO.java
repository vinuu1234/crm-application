package com.example.demo.dto;


import java.time.LocalDateTime;

import com.example.demo.entity.FollowUpStage;
import com.example.demo.entity.FollowUpStatus;
import com.example.demo.entity.FollowUpType;

import lombok.Data;

@Data
public class FollowUpDTO {
    private Long id;
    private Long leadId;
    private String leadName;
    private Long userId;
    private String userName;
    private FollowUpType type;
    private FollowUpStage stage;
    private String notes;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private FollowUpStatus status;
    private LocalDateTime nextFollowUp;
    private LocalDateTime createdAt;
}