package com.antiscalping.tickets.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets", indexes = {
    @Index(name = "idx_event_id", columnList = "event_id"),
    @Index(name = "idx_owner_id", columnList = "owner_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_qr_seed", columnList = "qr_seed")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
    
    @Column(nullable = false, unique = true)
    private String ticketNumber;
    
    @Column(nullable = false)
    private String qrSeed;
    
    private Integer transferCount;
    
    private LocalDateTime purchasedAt;
    
    private LocalDateTime transferredAt;
    
    private String transferredFrom;
    
    private LocalDateTime validatedAt;
    
    private Boolean isPooled;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = TicketStatus.AVAILABLE;
        transferCount = 0;
        isPooled = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum TicketStatus {
        AVAILABLE, TRANSFERRED, CLAIMED, VALIDATED, CANCELLED, REFUNDED
    }
}
