package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "follow_ups",
       indexes = {
           @Index(name = "idx_lead_id", columnList = "lead_id"),
           @Index(name = "idx_user_id", columnList = "user_id"),
           @Index(name = "idx_scheduled_date", columnList = "scheduled_date"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_completed_date", columnList = "completed_date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowUp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FollowUpType type;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private FollowUpStage stage;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FollowUpStatus status = FollowUpStatus.PENDING;
    
    @Column(name = "next_follow_up")
    private LocalDateTime nextFollowUp;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}