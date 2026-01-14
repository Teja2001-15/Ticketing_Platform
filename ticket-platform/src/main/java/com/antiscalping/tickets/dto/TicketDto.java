package com.antiscalping.tickets.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDto {
    private Long id;
    private Long eventId;
    private String eventName;
    private Long userId;
    private String ticketNumber;
    private String status;
    private Integer transferCount;
    private LocalDateTime purchasedAt;
    private LocalDateTime validatedAt;
    private LocalDateTime createdAt;
}
