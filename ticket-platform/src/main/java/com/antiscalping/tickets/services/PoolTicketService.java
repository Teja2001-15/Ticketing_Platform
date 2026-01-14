package com.antiscalping.tickets.services;

import com.antiscalping.tickets.dto.PoolTicketDto;
import com.antiscalping.tickets.entities.*;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PoolTicketService {
    
    @Autowired
    private PoolTicketRepository poolTicketRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public PoolTicketDto addToPool(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not the owner of this ticket");
        }
        
        if (ticket.getIsPooled()) {
            throw new BadRequestException("Ticket is already in pool");
        }
        
        PoolTicket poolTicket = PoolTicket.builder()
            .ticket(ticket)
            .event(ticket.getEvent())
            .status(PoolTicket.PoolStatus.AVAILABLE)
            .build();
        
        ticket.setIsPooled(true);
        ticketRepository.save(ticket);
        
        PoolTicket savedPoolTicket = poolTicketRepository.save(poolTicket);
        
        logAudit(userId, "TICKET_ADDED_TO_POOL", "POOL_TICKET", savedPoolTicket.getId(), null);
        
        return mapToDto(savedPoolTicket);
    }
    
    public PoolTicketDto nominateUser(Long poolTicketId, Long nominatedUserId, Long nominerUserId) {
        PoolTicket poolTicket = poolTicketRepository.findById(poolTicketId)
            .orElseThrow(() -> new ResourceNotFoundException("Pool ticket not found"));
        
        if (!poolTicket.getStatus().equals(PoolTicket.PoolStatus.AVAILABLE)) {
            throw new BadRequestException("Ticket is not available for nomination");
        }
        
        User nominatedUser = userRepository.findById(nominatedUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        poolTicket.setNominatedUserId(nominatedUserId.toString());
        poolTicket.setNominationExpiresAt(LocalDateTime.now().plusMinutes(15));
        poolTicket.setStatus(PoolTicket.PoolStatus.NOMINATED);
        
        PoolTicket savedPoolTicket = poolTicketRepository.save(poolTicket);
        
        logAudit(nominerUserId, "USER_NOMINATED_FOR_POOL", "POOL_TICKET", poolTicketId, null);
        
        return mapToDto(savedPoolTicket);
    }
    
    public PoolTicketDto claimPoolTicket(Long poolTicketId, Long userId) {
        PoolTicket poolTicket = poolTicketRepository.findById(poolTicketId)
            .orElseThrow(() -> new ResourceNotFoundException("Pool ticket not found"));
        
        if (!poolTicket.getStatus().equals(PoolTicket.PoolStatus.NOMINATED)) {
            throw new BadRequestException("Ticket is not available for claiming");
        }
        
        if (!poolTicket.getNominatedUserId().equals(userId.toString())) {
            throw new BadRequestException("You are not nominated for this ticket");
        }
        
        if (poolTicket.getNominationExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Nomination period has expired");
        }
        
        Ticket ticket = poolTicket.getTicket();
        User claimingUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        ticket.setUser(claimingUser);
        ticket.setIsPooled(false);
        ticketRepository.save(ticket);
        
        poolTicket.setStatus(PoolTicket.PoolStatus.CLAIMED);
        poolTicket.setClaimedAt(LocalDateTime.now());
        
        PoolTicket savedPoolTicket = poolTicketRepository.save(poolTicket);
        
        logAudit(userId, "POOL_TICKET_CLAIMED", "POOL_TICKET", poolTicketId, null);
        
        return mapToDto(savedPoolTicket);
    }
    
    public List<PoolTicketDto> getAvailablePoolTickets(Long eventId) {
        List<PoolTicket> tickets = poolTicketRepository.findByEventIdAndStatus(eventId, "AVAILABLE");
        return tickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<PoolTicketDto> getAllPoolTickets(Long eventId) {
        List<PoolTicket> tickets = poolTicketRepository.findByEventId(eventId);
        return tickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    private PoolTicketDto mapToDto(PoolTicket poolTicket) {
        return PoolTicketDto.builder()
            .id(poolTicket.getId())
            .ticketId(poolTicket.getTicket().getId())
            .eventId(poolTicket.getEvent().getId())
            .eventName(poolTicket.getEvent().getName())
            .status(poolTicket.getStatus().toString())
            .addedAt(poolTicket.getAddedAt())
            .claimedAt(poolTicket.getClaimedAt())
            .build();
    }
    
    private void logAudit(Long userId, String action, String entityType, Long entityId, String details) {
        AuditLog auditLog = AuditLog.builder()
            .user(userRepository.findById(userId).orElse(null))
            .action(action)
            .entityType(entityType)
            .entityId(entityId)
            .details(details)
            .build();
        auditLogRepository.save(auditLog);
    }
}
