package com.antiscalping.tickets.repositories;

import com.antiscalping.tickets.entities.TicketTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketTransferRepository extends JpaRepository<TicketTransfer, Long> {
    
    List<TicketTransfer> findByFromUserId(Long userId);
    
    List<TicketTransfer> findByToUserId(Long userId);
    
    List<TicketTransfer> findByTicketId(Long ticketId);
    
    List<TicketTransfer> findByFromUserIdAndStatus(Long userId, String status);
    
    List<TicketTransfer> findByToUserIdAndStatus(Long userId, String status);
}
