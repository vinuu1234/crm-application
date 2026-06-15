package com.example.demo.controller;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.dto.FollowUpDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.FollowUpRepository;
import com.example.demo.repository.LeadRepository;
import com.example.demo.repository.UserRepository;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
public class DashboardController {
    
    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        if (currentUser.getRole() == Role.ADMIN) {
            // Admin stats - all data
            stats.setTotalLeads(leadRepository.count());
            stats.setActiveLeads(leadRepository.countByAssignedToId(null)); // Modify as needed
            stats.setFollowUpsDueToday(followUpRepository.findPendingFollowUpsByUser(null, LocalDateTime.now()).size());
            
            // Leads by stage
            List<Object[]> stageCounts = leadRepository.countLeadsByStage();
            Map<String, Long> leadsByStage = stageCounts.stream()
                .collect(Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
            stats.setLeadsByStage(leadsByStage);
            
        } else if (currentUser.getRole() == Role.MANAGER) {
            // Manager stats - team data
            List<User> teamMembers = userRepository.findByManagerId(currentUser.getId());
            long teamLeadsCount = 0;
            for (User member : teamMembers) {
                teamLeadsCount += leadRepository.countByAssignedToId(member.getId());
            }
            stats.setTotalLeads(teamLeadsCount);
            
            // Team performance
            Map<String, Object> teamPerformance = new HashMap<>();
            for (User member : teamMembers) {
                long leadsCount = leadRepository.countByAssignedToId(member.getId());
                long convertedLeads = leadRepository.countConvertedLeadsByUser(member.getId());
                double conversionRate = leadsCount > 0 ? (convertedLeads * 100.0 / leadsCount) : 0;
                
                Map<String, Object> memberStats = new HashMap<>();
                memberStats.put("leads", leadsCount);
                memberStats.put("converted", convertedLeads);
                memberStats.put("conversionRate", conversionRate);
                teamPerformance.put(member.getName(), memberStats);
            }
            stats.setTeamPerformance(teamPerformance);
            
        } else {
            // Employee stats - personal data
            long myLeads = leadRepository.countByAssignedToId(currentUser.getId());
            stats.setTotalLeads(myLeads);
            
            long completedFollowUps = followUpRepository.countCompletedFollowUps(
                currentUser.getId(),
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            );
            stats.setConversionRate(myLeads > 0 ? (completedFollowUps * 100.0 / myLeads) : 0);
            
            List<FollowUpDTO> pendingFollowUps = followUpRepository.findPendingFollowUpsByUser(
                currentUser.getId(), 
                LocalDateTime.now()
            ).stream().map(f -> {
                FollowUpDTO dto = new FollowUpDTO();
                dto.setId(f.getId());
                dto.setLeadName(f.getLead().getName());
                dto.setScheduledDate(f.getScheduledDate());
                return dto;
            }).collect(Collectors.toList());
            stats.setFollowUpsDueToday(pendingFollowUps.size());
        }
        
        return ResponseEntity.ok(stats);
    }
}