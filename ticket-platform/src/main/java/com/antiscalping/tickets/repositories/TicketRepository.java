package com.antiscalping.tickets.repositories;

import com.antiscalping.tickets.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    Optional<Ticket> findByTicketNumber(String ticketNumber);
    
    Optional<Ticket> findByQrSeed(String qrSeed);
    
    List<Ticket> findByUserId(Long userId);
    
    List<Ticket> findByUserIdAndStatus(Long userId, String status);
    
    List<Ticket> findByEventId(Long eventId);
    
    @Query("SELECT t FROM Ticket t WHERE t.event.id = :eventId AND t.status = 'AVAILABLE'")
    List<Ticket> findAvailableTicketsByEvent(Long eventId);
    
    long countByEventId(Long eventId);
    
    long countByEventIdAndStatus(Long eventId, String status);
}
