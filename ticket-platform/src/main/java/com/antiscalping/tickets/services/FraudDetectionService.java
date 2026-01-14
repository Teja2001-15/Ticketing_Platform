package com.antiscalping.tickets.services;

import com.antiscalping.tickets.exceptions.FraudDetectedException;
import com.antiscalping.tickets.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FraudDetectionService {
    
    @Autowired
    private TicketRepository ticketRepository;
    
    private static final int MAX_TRANSFERS_ALLOWED = 3;
    private static final int VELOCITY_LIMIT = 5;
    
    public void validateTransfer(Long ticketId, Long userId) throws FraudDetectedException {
        long transferCount = ticketRepository.findById(ticketId)
            .map(t -> t.getTransferCount().longValue())
            .orElse(0L);
        
        if (transferCount >= MAX_TRANSFERS_ALLOWED) {
            log.warn("Fraud detected: Ticket {} exceeded max transfers", ticketId);
            throw new FraudDetectedException("Ticket has exceeded maximum transfer limit");
        }
    }
    
    public void validatePurchaseVelocity(Long userId) throws FraudDetectedException {
        long recentTickets = ticketRepository.findByUserId(userId).size();
        
        if (recentTickets > VELOCITY_LIMIT) {
            log.warn("Fraud detected: User {} has suspicious purchase velocity", userId);
            throw new FraudDetectedException("Purchase velocity too high. Please try again later.");
        }
    }
    
    public void validatePriceAnomaly(Double eventPrice, Double transferPrice) throws FraudDetectedException {
        if (transferPrice != null && eventPrice != null) {
            double priceIncrease = ((transferPrice - eventPrice) / eventPrice) * 100;
            
            if (priceIncrease > 50) {
                log.warn("Fraud detected: Ticket price increased by {}%", priceIncrease);
                throw new FraudDetectedException("Price anomaly detected. Transfer price is unreasonably high.");
            }
        }
    }
}
