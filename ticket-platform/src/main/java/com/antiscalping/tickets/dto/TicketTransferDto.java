package com.antiscalping.tickets.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTransferDto {
    
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
    
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;
    
    private String transferType;
    
    private Double transferPrice;
    
    private String notes;
}
