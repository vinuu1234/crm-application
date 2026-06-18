package com.example.demo.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.LeadDTO;
import com.example.demo.entity.Lead;
import com.example.demo.entity.User;
import com.example.demo.repository.LeadRepository;
import com.example.demo.repository.UserRepository;

@Service
public class LeadService {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public LeadDTO createLead(Lead lead, Long assignedById) {
        User assignedBy = userRepository.findById(assignedById)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + assignedById));
        lead.setAssignedBy(assignedBy);
        
   
        
        Lead savedLead = leadRepository.save(lead);
        return convertToDTO(savedLead);
    }
    
    public List<LeadDTO> getAllLeads() {
        return leadRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<LeadDTO> getLeadsByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return leadRepository.findByAssignedTo(user).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<LeadDTO> getLeadsByManager(Long managerId) {
        return leadRepository.findLeadsByManagerId(managerId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public LeadDTO getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
        return convertToDTO(lead);
    }
    
    @Transactional
    public LeadDTO updateLead(Long id, Lead leadDetails) {
        Lead lead = leadRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
        
        lead.setName(leadDetails.getName());
        lead.setEmail(leadDetails.getEmail());
        lead.setPhone(leadDetails.getPhone());
        lead.setCompany(leadDetails.getCompany());
        lead.setAddress(leadDetails.getAddress());
        lead.setSource(leadDetails.getSource());
        lead.setStatus(leadDetails.getStatus());
        lead.setCurrentStage(leadDetails.getCurrentStage());
        
        if (leadDetails.getAssignedTo() != null) {
            lead.setAssignedTo(leadDetails.getAssignedTo());
        }
        
        Lead updatedLead = leadRepository.save(lead);
        return convertToDTO(updatedLead);
    }
    
    @Transactional
    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }
    
    @Transactional
    public LeadDTO reassignLead(Long leadId, Long newUserId) {
        Lead lead = leadRepository.findById(leadId)
            .orElseThrow(() -> new RuntimeException("Lead not found with id: " + leadId));
        User newUser = userRepository.findById(newUserId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + newUserId));
        
        lead.setAssignedTo(newUser);
        Lead updatedLead = leadRepository.save(lead);
        return convertToDTO(updatedLead);
    }
    
    private LeadDTO convertToDTO(Lead lead) {
        LeadDTO dto = new LeadDTO();
        dto.setId(lead.getId());
        dto.setName(lead.getName());
        dto.setEmail(lead.getEmail());
        dto.setPhone(lead.getPhone());
        dto.setCompany(lead.getCompany());
        dto.setAddress(lead.getAddress());
        dto.setSource(lead.getSource());
        dto.setStatus(lead.getStatus());
        dto.setCurrentStage(lead.getCurrentStage());
        dto.setAssignedToId(lead.getAssignedTo().getId());
        dto.setAssignedToName(lead.getAssignedTo().getName());
        
        if (lead.getAssignedBy() != null) {
            dto.setAssignedById(lead.getAssignedBy().getId());
            dto.setAssignedByName(lead.getAssignedBy().getName());
        }
        
        dto.setCreatedAt(lead.getCreatedAt());
        dto.setUpdatedAt(lead.getUpdatedAt());
        
        return dto;
    }
}