package com.antiscalping.tickets.controllers;

import com.antiscalping.tickets.dto.TicketTransferDto;
import com.antiscalping.tickets.dto.ApiResponseDto;
import com.antiscalping.tickets.entities.TicketTransfer;
import com.antiscalping.tickets.services.TicketTransferService;
import com.antiscalping.tickets.services.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/transfers")
@CrossOrigin(origins = "*")
@Slf4j
public class TicketTransferController {
    
    @Autowired
    private TicketTransferService transferService;
    
    @Autowired
    private FraudDetectionService fraudDetectionService;
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<String>> initiateTransfer(@Valid @RequestBody TicketTransferDto transferDto) {
        try {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            
            // Fraud check
            fraudDetectionService.validateTransfer(transferDto.getTicketId(), userId);
            
            TicketTransfer transfer = transferService.initiateTransfer(userId, transferDto);
            
            ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .success(true)
                .message("Transfer initiated successfully")
                .data("Transfer ID: " + transfer.getId())
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Transfer initiation error", e);
            throw new RuntimeException(e);
        }
    }
    
    @PostMapping("/{transferId}/approve")
    public ResponseEntity<ApiResponseDto<String>> approveTransfer(@PathVariable Long transferId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        transferService.approveTransfer(transferId, userId);
        
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
            .success(true)
            .message("Transfer approved successfully")
            .data("Transfer is approved and waiting for completion")
            .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{transferId}/complete")
    public ResponseEntity<ApiResponseDto<String>> completeTransfer(@PathVariable Long transferId) {
        transferService.completeTransfer(transferId);
        
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
            .success(true)
            .message("Transfer completed successfully")
            .data("Ticket ownership has been transferred")
            .build();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{transferId}/reject")
    public ResponseEntity<ApiResponseDto<String>> rejectTransfer(@PathVariable Long transferId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        transferService.rejectTransfer(transferId, userId);
        
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
            .success(true)
            .message("Transfer rejected successfully")
            .data("Transfer has been rejected")
            .build();
        return ResponseEntity.ok(response);
    }
}
