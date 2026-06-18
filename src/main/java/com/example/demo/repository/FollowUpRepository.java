package com.example.demo.repository;

import com.example.demo.entity.FollowUp;
import com.example.demo.entity.FollowUpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, Long> {
    
    // ✅ Get pending follow-ups for a user
    @Query("SELECT f FROM FollowUp f WHERE f.user.id = :userId AND f.status = 'PENDING' AND (f.scheduledDate <= :date OR f.scheduledDate IS NULL)")
    List<FollowUp> findPendingFollowUpsByUser(@Param("userId") Long userId, @Param("date") LocalDateTime date);
    
    // ✅ Get ALL pending follow-ups (Admin)
    @Query("SELECT f FROM FollowUp f WHERE f.status = 'PENDING' AND (f.scheduledDate <= :date OR f.scheduledDate IS NULL)")
    List<FollowUp> findAllPendingFollowUps(@Param("date") LocalDateTime date);
    
    // ✅ Get team pending follow-ups (Manager)
    @Query("SELECT f FROM FollowUp f WHERE f.user.manager.id = :managerId AND f.status = 'PENDING' AND (f.scheduledDate <= :date OR f.scheduledDate IS NULL)")
    List<FollowUp> findTeamPendingFollowUps(@Param("managerId") Long managerId, @Param("date") LocalDateTime date);
    
    // ✅ Get follow-ups by lead ID
    @Query("SELECT f FROM FollowUp f WHERE f.lead.id = :leadId ORDER BY f.createdAt DESC")
    List<FollowUp> findByLeadId(@Param("leadId") Long leadId);
    
    // ✅ Get completed follow-ups count
    @Query("SELECT COUNT(f) FROM FollowUp f WHERE f.user.id = :userId AND f.status = 'COMPLETED' AND f.completedDate BETWEEN :startDate AND :endDate")
    long countCompletedFollowUps(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ✅ Get follow-ups by status
    @Query("SELECT f FROM FollowUp f WHERE f.status = :status")
    List<FollowUp> findByStatus(@Param("status") FollowUpStatus status);
}