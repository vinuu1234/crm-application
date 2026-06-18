package com.example.demo.controller;

import com.example.demo.dto.FollowUpDTO;
import com.example.demo.dto.LeadFollowUpDTO;
import com.example.demo.entity.FollowUp;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.service.FollowUpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/followups")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3003"}, allowCredentials = "true")
public class FollowUpController {
    
    @Autowired
    private FollowUpService followUpService;
    
    // ✅ CREATE FOLLOW-UP
    @PostMapping("/create/{leadId}")
    public ResponseEntity<?> createFollowUp(
            @PathVariable Long leadId, 
            @Valid @RequestBody FollowUp followUp, 
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            FollowUpDTO createdFollowUp = followUpService.createFollowUp(followUp, leadId, currentUser.getId());
            return ResponseEntity.ok(createdFollowUp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ GET FOLLOW-UPS FOR A SPECIFIC LEAD
    @GetMapping("/lead/{leadId}")
    public ResponseEntity<?> getFollowUpsByLead(@PathVariable Long leadId) {
        try {
            List<FollowUpDTO> followUps = followUpService.getFollowUpsByLead(leadId);
            return ResponseEntity.ok(followUps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ GET ALL FOLLOW-UPS GROUPED BY LEAD (NEW)
    @GetMapping("/all-by-lead")
    public ResponseEntity<?> getAllFollowUpsByLead(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        System.out.println("🔍 Getting follow-ups by lead for: " + currentUser.getEmail() + " (Role: " + currentUser.getRole() + ")");
        
        List<LeadFollowUpDTO> result;
        
        if (currentUser.getRole() == Role.ADMIN) {
            // Admin sees ALL leads with their follow-ups
            result = followUpService.getAllLeadsWithFollowUps();
            System.out.println("🔍 Admin found " + result.size() + " leads with follow-ups");
        } else if (currentUser.getRole() == Role.MANAGER) {
            // Manager sees team leads with follow-ups
            result = followUpService.getTeamLeadsWithFollowUps(currentUser.getId());
            System.out.println("🔍 Manager found " + result.size() + " team leads with follow-ups");
        } else {
            // Employee sees only their leads with follow-ups
            result = followUpService.getUserLeadsWithFollowUps(currentUser.getId());
            System.out.println("🔍 Employee found " + result.size() + " leads with follow-ups");
        }
        
        return ResponseEntity.ok(result);
    }
    
    // ✅ GET PENDING FOLLOW-UPS (Role-based)
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingFollowUps(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        System.out.println("🔍 Current user: " + currentUser.getEmail() + " (ID: " + currentUser.getId() + ", Role: " + currentUser.getRole() + ")");
        
        List<FollowUpDTO> pendingFollowUps;
        
        if (currentUser.getRole() == Role.ADMIN) {
            pendingFollowUps = followUpService.getAllPendingFollowUps();
            System.out.println("🔍 Admin found " + pendingFollowUps.size() + " total pending follow-ups");
        } else if (currentUser.getRole() == Role.MANAGER) {
            pendingFollowUps = followUpService.getTeamPendingFollowUps(currentUser.getId());
            System.out.println("🔍 Manager found " + pendingFollowUps.size() + " team pending follow-ups");
        } else {
            pendingFollowUps = followUpService.getPendingFollowUpsByUser(currentUser.getId());
            System.out.println("🔍 Employee found " + pendingFollowUps.size() + " pending follow-ups");
        }
        
        return ResponseEntity.ok(pendingFollowUps);
    }
    
    // ✅ GET ALL FOLLOW-UPS (Admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllFollowUps(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Admin only."));
        }
        
        try {
            List<FollowUpDTO> allFollowUps = followUpService.getAllFollowUps();
            System.out.println("🔍 Admin found " + allFollowUps.size() + " total follow-ups");
            return ResponseEntity.ok(allFollowUps);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ COMPLETE A FOLLOW-UP
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeFollowUp(@PathVariable Long id, Authentication authentication) {
        try {
            FollowUpDTO completedFollowUp = followUpService.completeFollowUp(id);
            return ResponseEntity.ok(completedFollowUp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // ✅ DELETE A FOLLOW-UP (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFollowUp(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied. Admin only."));
        }
        
        try {
            followUpService.deleteFollowUp(id);
            return ResponseEntity.ok(Map.of("message", "Follow-up deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}