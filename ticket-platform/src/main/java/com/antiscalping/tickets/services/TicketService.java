package com.antiscalping.tickets.services;

import com.antiscalping.tickets.dto.TicketDto;
import com.antiscalping.tickets.dto.TicketPurchaseDto;
import com.antiscalping.tickets.entities.Ticket;
import com.antiscalping.tickets.entities.Event;
import com.antiscalping.tickets.entities.User;
import com.antiscalping.tickets.entities.AuditLog;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.*;
import com.antiscalping.tickets.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TicketService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private SecurityUtils securityUtils;
    
    public List<TicketDto> purchaseTickets(Long userId, TicketPurchaseDto purchaseDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Event event = eventRepository.findById(purchaseDto.getEventId())
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        if (event.getAvailableTickets() < purchaseDto.getQuantity()) {
            throw new BadRequestException("Not enough tickets available");
        }
        
        List<Ticket> purchasedTickets = new ArrayList<>();
        
        for (int i = 0; i < purchaseDto.getQuantity(); i++) {
            Ticket ticket = Ticket.builder()
                .event(event)
                .user(user)
                .status(Ticket.TicketStatus.AVAILABLE)
                .ticketNumber(securityUtils.generateTicketNumber(event.getId(), (long)(Math.random() * 1000000)))
                .qrSeed(securityUtils.generateQRSeed())
                .transferCount(0)
                .purchasedAt(LocalDateTime.now())
                .build();
            
            Ticket savedTicket = ticketRepository.save(ticket);
            purchasedTickets.add(savedTicket);
            
            logAudit(userId, "TICKET_PURCHASED", "TICKET", savedTicket.getId(), null);
        }
        
        event.setAvailableTickets(event.getAvailableTickets() - purchaseDto.getQuantity());
        eventRepository.save(event);
        
        return purchasedTickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<TicketDto> getMyTickets(Long userId) {
        List<Ticket> tickets = ticketRepository.findByUserId(userId);
        return tickets.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public TicketDto getTicketById(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return mapToDto(ticket);
    }
    
    public TicketDto getTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return mapToDto(ticket);
    }
    
    public void validateTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not the owner of this ticket");
        }
        
        ticket.setStatus(Ticket.TicketStatus.VALIDATED);
        ticket.setValidatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
        
        logAudit(userId, "TICKET_VALIDATED", "TICKET", ticketId, null);
    }
    
    public void cancelTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not the owner of this ticket");
        }
        
        if (!ticket.getStatus().equals(Ticket.TicketStatus.AVAILABLE)) {
            throw new BadRequestException("Ticket cannot be cancelled in current status");
        }
        
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
        
        Event event = ticket.getEvent();
        event.setAvailableTickets(event.getAvailableTickets() + 1);
        eventRepository.save(event);
        
        logAudit(userId, "TICKET_CANCELLED", "TICKET", ticketId, null);
    }
    
    private TicketDto mapToDto(Ticket ticket) {
        return TicketDto.builder()
            .id(ticket.getId())
            .eventId(ticket.getEvent().getId())
            .eventName(ticket.getEvent().getName())
            .userId(ticket.getUser().getId())
            .ticketNumber(ticket.getTicketNumber())
            .status(ticket.getStatus().toString())
            .transferCount(ticket.getTransferCount())
            .purchasedAt(ticket.getPurchasedAt())
            .validatedAt(ticket.getValidatedAt())
            .createdAt(ticket.getCreatedAt())
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
