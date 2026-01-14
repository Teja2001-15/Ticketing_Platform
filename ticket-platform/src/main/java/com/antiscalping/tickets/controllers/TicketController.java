package com.antiscalping.tickets.controllers;

import com.antiscalping.tickets.dto.TicketDto;
import com.antiscalping.tickets.dto.TicketPurchaseDto;
import com.antiscalping.tickets.dto.ApiResponseDto;
import com.antiscalping.tickets.services.TicketService;
import com.antiscalping.tickets.services.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/tickets")
@CrossOrigin(origins = "*")
@Slf4j
public class TicketController {
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    @PostMapping("/purchase")
    public ResponseEntity<ApiResponseDto<List<TicketDto>>> purchaseTickets(@Valid @RequestBody TicketPurchaseDto purchaseDto) {
        try {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            
            // Fraud check
            fraudDetectionService.validatePurchaseVelocity(userId);
            
            List<TicketDto> tickets = ticketService.purchaseTickets(userId, purchaseDto);
            
            ApiResponseDto<List<TicketDto>> response = ApiResponseDto.<List<TicketDto>>builder()
                .success(true)
                .message("Tickets purchased successfully")
                .data(tickets)
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Ticket purchase error", e);
            throw new RuntimeException(e);
        }
    }
    
    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponseDto<List<TicketDto>>> getMyTickets() {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        List<TicketDto> tickets = ticketService.getMyTickets(userId);
        
        ApiResponseDto<List<TicketDto>> response = ApiResponseDto.<List<TicketDto>>builder()
            .success(true)
            .message("Tickets retrieved successfully")
            .data(tickets)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponseDto<TicketDto>> getTicketById(@PathVariable Long ticketId) {
        TicketDto ticketDto = ticketService.getTicketById(ticketId);
        
        ApiResponseDto<TicketDto> response = ApiResponseDto.<TicketDto>builder()
            .success(true)
            .message("Ticket retrieved successfully")
            .data(ticketDto)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{ticketId}/validate")
    public ResponseEntity<ApiResponseDto<String>> validateTicket(@PathVariable Long ticketId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        ticketService.validateTicket(ticketId, userId);
        
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
            .success(true)
            .message("Ticket validated successfully")
            .data("Ticket is valid and ready for entry")
            .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{ticketId}/cancel")
    public ResponseEntity<ApiResponseDto<String>> cancelTicket(@PathVariable Long ticketId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        ticketService.cancelTicket(ticketId, userId);
        
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
            .success(true)
            .message("Ticket cancelled successfully")
            .data("Refund will be processed")
            .build();
        return ResponseEntity.ok(response);
    }
}
