package com.example.demo.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FollowUpStage;
import com.example.demo.entity.Lead;
import com.example.demo.entity.User;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    List<Lead> findByAssignedTo(User user);
    List<Lead> findByAssignedToAndCurrentStage(User user, FollowUpStage stage);
    List<Lead> findByAssignedToId(Long userId);
    
    @Query("SELECT l FROM Lead l WHERE l.assignedTo.manager.id = :managerId")
    List<Lead> findLeadsByManagerId(@Param("managerId") Long managerId);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.assignedTo.id = :userId")
    long countByAssignedToId(@Param("userId") Long userId);
    
    @Query("SELECT l.currentStage, COUNT(l) FROM Lead l GROUP BY l.currentStage")
    List<Object[]> countLeadsByStage();
    
    @Query("SELECT l FROM Lead l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<Lead> findLeadsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(l) FROM Lead l WHERE l.status = 'CONVERTED' AND l.assignedTo.id = :userId")
    long countConvertedLeadsByUser(@Param("userId") Long userId);
}