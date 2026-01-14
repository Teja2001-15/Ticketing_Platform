package com.antiscalping.tickets.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketPurchaseDto {
    
    @NotNull(message = "Event ID is required")
    private Long eventId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10, message = "Maximum 10 tickets per purchase")
    private Integer quantity;
}
