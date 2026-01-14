package com.antiscalping.tickets.controllers;

import com.antiscalping.tickets.dto.PoolTicketDto;
import com.antiscalping.tickets.dto.ApiResponseDto;
import com.antiscalping.tickets.services.PoolTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/pool")
@CrossOrigin(origins = "*")
@Slf4j
public class PoolTicketController {
    
    @Autowired
    private PoolTicketService poolTicketService;
    
    @PostMapping("/{ticketId}/add")
    public ResponseEntity<ApiResponseDto<PoolTicketDto>> addToPool(@PathVariable Long ticketId) {
        try {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            PoolTicketDto poolTicket = poolTicketService.addToPool(ticketId, userId);
            
            ApiResponseDto<PoolTicketDto> response = ApiResponseDto.<PoolTicketDto>builder()
                .success(true)
                .message("Ticket added to pool successfully")
                .data(poolTicket)
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Add to pool error", e);
            throw new RuntimeException(e);
        }
    }
    
    @PostMapping("/{poolTicketId}/nominate/{nominatedUserId}")
    public ResponseEntity<ApiResponseDto<PoolTicketDto>> nominateUser(
            @PathVariable Long poolTicketId,
            @PathVariable Long nominatedUserId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        PoolTicketDto poolTicket = poolTicketService.nominateUser(poolTicketId, nominatedUserId, userId);
        
        ApiResponseDto<PoolTicketDto> response = ApiResponseDto.<PoolTicketDto>builder()
            .success(true)
            .message("User nominated successfully. They have 15 minutes to claim the ticket.")
            .data(poolTicket)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{poolTicketId}/claim")
    public ResponseEntity<ApiResponseDto<PoolTicketDto>> claimTicket(@PathVariable Long poolTicketId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        PoolTicketDto poolTicket = poolTicketService.claimPoolTicket(poolTicketId, userId);
        
        ApiResponseDto<PoolTicketDto> response = ApiResponseDto.<PoolTicketDto>builder()
            .success(true)
            .message("Ticket claimed successfully")
            .data(poolTicket)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponseDto<List<PoolTicketDto>>> getAvailablePoolTickets(@PathVariable Long eventId) {
        List<PoolTicketDto> tickets = poolTicketService.getAvailablePoolTickets(eventId);
        
        ApiResponseDto<List<PoolTicketDto>> response = ApiResponseDto.<List<PoolTicketDto>>builder()
            .success(true)
            .message("Pool tickets retrieved successfully")
            .data(tickets)
            .build();
        return ResponseEntity.ok(response);
    }
}
