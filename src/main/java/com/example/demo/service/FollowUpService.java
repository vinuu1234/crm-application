package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.FollowUpDTO;
import com.example.demo.entity.FollowUp;
import com.example.demo.entity.FollowUpStatus;
import com.example.demo.entity.Lead;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowUpRepository;
import com.example.demo.repository.LeadRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FollowUpService {
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public FollowUpDTO createFollowUp(FollowUp followUp, Long leadId, Long userId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadId));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        followUp.setLead(lead);
        followUp.setUser(user);
        
        // Update lead stage if changed
        if (followUp.getStage() != null) {
            lead.setCurrentStage(followUp.getStage());
            leadRepository.save(lead);
        }
        
        FollowUp savedFollowUp = followUpRepository.save(followUp);
        return convertToDTO(savedFollowUp);
    }
    
    public List<FollowUpDTO> getFollowUpsByLead(Long leadId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadId));
        return followUpRepository.findByLead(lead).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<FollowUpDTO> getPendingFollowUpsByUser(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return followUpRepository.findPendingFollowUpsByUser(userId, now).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<FollowUpDTO> getTeamPendingFollowUps(Long managerId) {
        LocalDateTime now = LocalDateTime.now();
        return followUpRepository.findTeamPendingFollowUps(managerId, now).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public FollowUpDTO completeFollowUp(Long id) {
        FollowUp followUp = followUpRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Follow-up not found with id: " + id));
        
        followUp.setStatus(FollowUpStatus.COMPLETED);
        followUp.setCompletedDate(LocalDateTime.now());
        
        // Schedule next follow-up if needed
        if (followUp.getNextFollowUp() != null) {
            FollowUp nextFollowUp = new FollowUp();
            nextFollowUp.setLead(followUp.getLead());
            nextFollowUp.setUser(followUp.getUser());
            nextFollowUp.setType(followUp.getType());
            nextFollowUp.setScheduledDate(followUp.getNextFollowUp());
            nextFollowUp.setStage(followUp.getStage());
            followUpRepository.save(nextFollowUp);
        }
        
        FollowUp updatedFollowUp = followUpRepository.save(followUp);
        return convertToDTO(updatedFollowUp);
    }
    
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