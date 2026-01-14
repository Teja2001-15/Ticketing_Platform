package com.antiscalping.tickets.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trusted_circles", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "trusted_user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrustedCircle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trusted_user_id", nullable = false)
    private User trustedUser;
    
    @Column(nullable = false)
    private String relationship;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
