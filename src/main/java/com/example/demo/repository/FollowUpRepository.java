package com.example.demo.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FollowUp;
import com.example.demo.entity.FollowUpStatus;
import com.example.demo.entity.Lead;
import com.example.demo.entity.User;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    List<FollowUp> findByLead(Lead lead);
    List<FollowUp> findByUser(User user);
    List<FollowUp> findByStatus(FollowUpStatus status);
    
    @Query("SELECT f FROM FollowUp f WHERE f.user.id = :userId AND f.status = 'PENDING' AND f.scheduledDate <= :date")
    List<FollowUp> findPendingFollowUpsByUser(@Param("userId") Long userId, @Param("date") LocalDateTime date);
    
    @Query("SELECT f FROM FollowUp f WHERE f.user.manager.id = :managerId AND f.status = 'PENDING' AND f.scheduledDate <= :date")
    List<FollowUp> findTeamPendingFollowUps(@Param("managerId") Long managerId, @Param("date") LocalDateTime date);
    
    @Query("SELECT COUNT(f) FROM FollowUp f WHERE f.user.id = :userId AND f.status = 'COMPLETED' AND f.completedDate BETWEEN :startDate AND :endDate")
    long countCompletedFollowUps(@Param("userId") Long userId, 
                                  @Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT f FROM FollowUp f WHERE f.user.id = :userId AND f.scheduledDate BETWEEN :startDate AND :endDate")
    List<FollowUp> findFollowUpsByDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT f.type, COUNT(f) FROM FollowUp f WHERE f.user.id = :userId GROUP BY f.type")
    List<Object[]> countFollowUpsByType(@Param("userId") Long userId);
}