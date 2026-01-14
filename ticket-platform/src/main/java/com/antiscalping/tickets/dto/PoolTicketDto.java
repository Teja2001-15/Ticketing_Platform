package com.antiscalping.tickets.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PoolTicketDto {
    private Long id;
    private Long ticketId;
    private Long eventId;
    private String eventName;
    private String status;
    private LocalDateTime addedAt;
    private LocalDateTime claimedAt;
}
