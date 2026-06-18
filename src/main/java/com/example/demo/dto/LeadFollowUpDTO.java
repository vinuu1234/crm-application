package com.example.demo.dto;


import lombok.Data;
import java.util.List;

@Data
public class LeadFollowUpDTO {
    private Long leadId;
    private String leadName;
    private String leadEmail;
    private String leadCompany;
    private String leadStage;
    private String assignedToName;
    private List<FollowUpDTO> followUps;
}