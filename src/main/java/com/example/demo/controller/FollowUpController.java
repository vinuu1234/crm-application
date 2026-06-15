package com.example.demo.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FollowUpDTO;
import com.example.demo.entity.FollowUp;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.FollowUpService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/followups")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class FollowUpController {
    
    @Autowired
    private FollowUpService followUpService;
    
    @PostMapping("/create/{leadId}")
    public ResponseEntity<?> createFollowUp(@PathVariable Long leadId, @Valid @RequestBody FollowUp followUp, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            FollowUpDTO createdFollowUp = followUpService.createFollowUp(followUp, leadId, currentUser.getId());
            return ResponseEntity.ok(createdFollowUp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<?> getFollowUpsByLead(@PathVariable Long leadId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            List<FollowUpDTO> followUps = followUpService.getFollowUpsByLead(leadId);
            return ResponseEntity.ok(followUps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingFollowUps(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<FollowUpDTO> pendingFollowUps;
        
        if (currentUser.getRole() == Role.MANAGER) {
            pendingFollowUps = followUpService.getTeamPendingFollowUps(currentUser.getId());
        } else {
            pendingFollowUps = followUpService.getPendingFollowUpsByUser(currentUser.getId());
        }
        
        return ResponseEntity.ok(pendingFollowUps);
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeFollowUp(@PathVariable Long id, Authentication authentication) {
        try {
            FollowUpDTO completedFollowUp = followUpService.completeFollowUp(id);
            return ResponseEntity.ok(completedFollowUp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}