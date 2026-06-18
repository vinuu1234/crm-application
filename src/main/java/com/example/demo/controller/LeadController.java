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

import com.example.demo.dto.LeadDTO;
import com.example.demo.entity.Lead;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.LeadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3003"}, allowCredentials = "true")
public class LeadController {
    
    @Autowired
    private LeadService leadService;
    
    @PostMapping("/create")
    public ResponseEntity<?> createLead(@Valid @RequestBody Lead lead, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Only Admin and Manager can create leads
        if (currentUser.getRole() == Role.EMPLOYEE) {
            return ResponseEntity.status(403).body(Map.of("error", "Employees cannot create leads"));
        }
        
        try {
            LeadDTO createdLead = leadService.createLead(lead, currentUser.getId());
            return ResponseEntity.ok(createdLead);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeads(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<LeadDTO> leads;
        
        if (currentUser.getRole() == Role.ADMIN) {
            leads = leadService.getAllLeads();
        } else if (currentUser.getRole() == Role.MANAGER) {
            leads = leadService.getLeadsByManager(currentUser.getId());
        } else {
            leads = leadService.getLeadsByUser(currentUser.getId());
        }
        
        return ResponseEntity.ok(leads);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            LeadDTO lead = leadService.getLeadById(id);
            
            // Check access permissions
            if (currentUser.getRole() == Role.ADMIN) {
                return ResponseEntity.ok(lead);
            } else if (currentUser.getRole() == Role.MANAGER) {
                // Manager can see team leads
                List<LeadDTO> teamLeads = leadService.getLeadsByManager(currentUser.getId());
                boolean hasAccess = teamLeads.stream().anyMatch(l -> l.getId().equals(id));
                if (hasAccess) {
                    return ResponseEntity.ok(lead);
                }
            } else if (lead.getAssignedToId().equals(currentUser.getId())) {
                return ResponseEntity.ok(lead);
            }
            
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(@PathVariable Long id, @Valid @RequestBody Lead leadDetails, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            LeadDTO existingLead = leadService.getLeadById(id);
            
            // Check permissions
            if (currentUser.getRole() == Role.ADMIN) {
                LeadDTO updatedLead = leadService.updateLead(id, leadDetails);
                return ResponseEntity.ok(updatedLead);
            } else if (currentUser.getRole() == Role.MANAGER) {
                List<LeadDTO> teamLeads = leadService.getLeadsByManager(currentUser.getId());
                boolean hasAccess = teamLeads.stream().anyMatch(l -> l.getId().equals(id));
                if (hasAccess) {
                    LeadDTO updatedLead = leadService.updateLead(id, leadDetails);
                    return ResponseEntity.ok(updatedLead);
                }
            } else if (existingLead.getAssignedToId().equals(currentUser.getId())) {
                LeadDTO updatedLead = leadService.updateLead(id, leadDetails);
                return ResponseEntity.ok(updatedLead);
            }
            
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Only Admin can delete leads
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Only Admin can delete leads"));
        }
        
        try {
            leadService.deleteLead(id);
            return ResponseEntity.ok(Map.of("message", "Lead deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{leadId}/reassign/{userId}")
    public ResponseEntity<?> reassignLead(@PathVariable Long leadId, @PathVariable Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Admin and Manager can reassign leads
        if (currentUser.getRole() == Role.EMPLOYEE) {
            return ResponseEntity.status(403).body(Map.of("error", "Employees cannot reassign leads"));
        }
        
        try {
            LeadDTO reassignedLead = leadService.reassignLead(leadId, userId);
            return ResponseEntity.ok(reassignedLead);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/my-leads")
    public ResponseEntity<?> getMyLeads(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<LeadDTO> leads = leadService.getLeadsByUser(currentUser.getId());
        return ResponseEntity.ok(leads);
    }
}