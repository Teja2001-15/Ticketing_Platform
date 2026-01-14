package com.antiscalping.tickets.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pool_tickets", indexes = {
    @Index(name = "idx_pool_ticket_event", columnList = "event_id"),
    @Index(name = "idx_pool_ticket_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoolTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PoolStatus status;
    
    private String nominatedUserId;
    
    private LocalDateTime nominationExpiresAt;
    
    private LocalDateTime addedAt;
    
    private LocalDateTime claimedAt;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        addedAt = LocalDateTime.now();
        status = PoolStatus.AVAILABLE;
    }
    
    public enum PoolStatus {
        AVAILABLE, NOMINATED, CLAIMED, EXPIRED
    }
}
