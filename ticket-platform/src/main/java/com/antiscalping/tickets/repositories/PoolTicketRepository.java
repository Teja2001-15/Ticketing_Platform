package com.antiscalping.tickets.repositories;

import com.antiscalping.tickets.entities.PoolTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PoolTicketRepository extends JpaRepository<PoolTicket, Long> {
    
    List<PoolTicket> findByEventId(Long eventId);
    
    List<PoolTicket> findByEventIdAndStatus(Long eventId, String status);
    
    List<PoolTicket> findByStatus(String status);
    
    long countByEventIdAndStatus(Long eventId, String status);
}
