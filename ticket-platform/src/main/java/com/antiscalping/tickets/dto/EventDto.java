package com.antiscalping.tickets.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;
    
    @NotBlank(message = "Event name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Venue is required")
    private String venue;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer totalCapacity;
    
    private Integer availableTickets;
    
    @NotNull(message = "Ticket price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double ticketPrice;
    
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
