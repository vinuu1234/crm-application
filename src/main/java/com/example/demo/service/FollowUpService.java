package com.example.demo.service;

import com.example.demo.dto.FollowUpDTO;
import com.example.demo.dto.LeadFollowUpDTO;
import com.example.demo.entity.FollowUp;
import com.example.demo.entity.Lead;
import com.example.demo.entity.User;
import com.example.demo.entity.FollowUpStatus;
import com.example.demo.repository.FollowUpRepository;
import com.example.demo.repository.LeadRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowUpService {
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ✅ CREATE
    @Transactional
    public FollowUpDTO createFollowUp(FollowUp followUp, Long leadId, Long userId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadId));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        followUp.setLead(lead);
        followUp.setUser(user);
        
        if (followUp.getStage() != null) {
            lead.setCurrentStage(followUp.getStage());
            leadRepository.save(lead);
        }
        
        FollowUp savedFollowUp = followUpRepository.save(followUp);
        return convertToDTO(savedFollowUp);
    }
    
    // ✅ GET ALL LEADS WITH THEIR FOLLOW-UPS (ADMIN)
    public List<LeadFollowUpDTO> getAllLeadsWithFollowUps() {
        List<Lead> leads = leadRepository.findAll();
        List<LeadFollowUpDTO> result = new ArrayList<>();
        
        for (Lead lead : leads) {
            LeadFollowUpDTO dto = new LeadFollowUpDTO();
            dto.setLeadId(lead.getId());
            dto.setLeadName(lead.getName());
            dto.setLeadEmail(lead.getEmail());
            dto.setLeadCompany(lead.getCompany());
            dto.setLeadStage(lead.getCurrentStage() != null ? lead.getCurrentStage().toString() : "N/A");
            dto.setAssignedToName(lead.getAssignedTo() != null ? lead.getAssignedTo().getName() : "Unassigned");
            
            List<FollowUpDTO> followUps = followUpRepository.findByLeadId(lead.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            dto.setFollowUps(followUps);
            
            result.add(dto);
        }
        
        return result;
    }
    
    // ✅ GET TEAM LEADS WITH FOLLOW-UPS (MANAGER)
    public List<LeadFollowUpDTO> getTeamLeadsWithFollowUps(Long managerId) {
        List<Lead> leads = leadRepository.findLeadsByManagerId(managerId);
        List<LeadFollowUpDTO> result = new ArrayList<>();
        
        for (Lead lead : leads) {
            LeadFollowUpDTO dto = new LeadFollowUpDTO();
            dto.setLeadId(lead.getId());
            dto.setLeadName(lead.getName());
            dto.setLeadEmail(lead.getEmail());
            dto.setLeadCompany(lead.getCompany());
            dto.setLeadStage(lead.getCurrentStage() != null ? lead.getCurrentStage().toString() : "N/A");
            dto.setAssignedToName(lead.getAssignedTo() != null ? lead.getAssignedTo().getName() : "Unassigned");
            
            List<FollowUpDTO> followUps = followUpRepository.findByLeadId(lead.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            dto.setFollowUps(followUps);
            
            result.add(dto);
        }
        
        return result;
    }
    
    // ✅ GET USER LEADS WITH FOLLOW-UPS (EMPLOYEE)
    public List<LeadFollowUpDTO> getUserLeadsWithFollowUps(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Lead> leads = leadRepository.findByAssignedTo(user);
        List<LeadFollowUpDTO> result = new ArrayList<>();
        
        for (Lead lead : leads) {
            LeadFollowUpDTO dto = new LeadFollowUpDTO();
            dto.setLeadId(lead.getId());
            dto.setLeadName(lead.getName());
            dto.setLeadEmail(lead.getEmail());
            dto.setLeadCompany(lead.getCompany());
            dto.setLeadStage(lead.getCurrentStage() != null ? lead.getCurrentStage().toString() : "N/A");
            dto.setAssignedToName(lead.getAssignedTo() != null ? lead.getAssignedTo().getName() : "Unassigned");
            
            List<FollowUpDTO> followUps = followUpRepository.findByLeadId(lead.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            dto.setFollowUps(followUps);
            
            result.add(dto);
        }
        
        return result;
    }
    
    // ✅ GET ALL FOLLOW-UPS (Admin only)
    public List<FollowUpDTO> getAllFollowUps() {
        return followUpRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ✅ GET ALL PENDING FOLLOW-UPS (Admin)
    public List<FollowUpDTO> getAllPendingFollowUps() {
        LocalDateTime now = LocalDateTime.now();
        List<FollowUp> followUps = followUpRepository.findAllPendingFollowUps(now);
        return followUps.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ✅ GET FOLLOW-UPS BY LEAD
    public List<FollowUpDTO> getFollowUpsByLead(Long leadId) {
        List<FollowUp> followUps = followUpRepository.findByLeadId(leadId);
        return followUps.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ✅ GET PENDING FOLLOW-UPS FOR A USER
    public List<FollowUpDTO> getPendingFollowUpsByUser(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<FollowUp> followUps = followUpRepository.findPendingFollowUpsByUser(userId, now);
        return followUps.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ✅ GET TEAM PENDING FOLLOW-UPS (Manager)
    public List<FollowUpDTO> getTeamPendingFollowUps(Long managerId) {
        LocalDateTime now = LocalDateTime.now();
        List<FollowUp> followUps = followUpRepository.findTeamPendingFollowUps(managerId, now);
        return followUps.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // ✅ COMPLETE FOLLOW-UP
    @Transactional
    public FollowUpDTO completeFollowUp(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Follow-up not found with id: " + id));
        
        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUp.setCompletedDate(LocalDateTime.now());
        
        if (followUp.getNextFollowUp() != null) {
            FollowUp next = new FollowUp();
            next.setLead(followUp.getLead());
            next.setUser(followUp.getUser());
            next.setType(followUp.getType());
            next.setStage(followUp.getStage());
            next.setScheduledDate(followUp.getNextFollowUp());
            next.setStatus(FollowUpStatus.PENDING);
            followUpRepository.save(next);
        }
        
        FollowUp updatedFollowUp = followUpRepository.save(followUp);
        return convertToDTO(updatedFollowUp);
    }
    
    // ✅ DELETE FOLLOW-UP (Admin only)
    @Transactional
    public void deleteFollowUp(Long id) {
        followUpRepository.deleteById(id);
    }
    
    // ✅ CONVERT TO DTO
    private FollowUpDTO convertToDTO(FollowUp followUp) {
        FollowUpDTO dto = new FollowUpDTO();
        dto.setId(followUp.getId());
        dto.setLeadId(followUp.getLead().getId());
        dto.setLeadName(followUp.getLead().getName());
        dto.setUserId(followUp.getUser().getId());
        dto.setUserName(followUp.getUser().getName());
        dto.setType(followUp.getType());
        dto.setStage(followUp.getStage());
        dto.setNotes(followUp.getNotes());
        dto.setScheduledDate(followUp.getScheduledDate());
        dto.setCompletedDate(followUp.getCompletedDate());
        dto.setStatus(followUp.getStatus());
        dto.setNextFollowUp(followUp.getNextFollowUp());
        dto.setCreatedAt(followUp.getCreatedAt());
        
        return dto;
    }
}