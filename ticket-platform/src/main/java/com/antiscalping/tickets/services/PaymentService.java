package com.antiscalping.tickets.services;

import com.antiscalping.tickets.entities.Payment;
import com.antiscalping.tickets.entities.User;
import com.antiscalping.tickets.entities.AuditLog;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.PaymentRepository;
import com.antiscalping.tickets.repositories.UserRepository;
import com.antiscalping.tickets.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public Payment processPayment(Long userId, Double amount, Payment.PaymentGateway gateway) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (amount <= 0) {
            throw new BadRequestException("Payment amount must be greater than 0");
        }
        
        Payment payment = Payment.builder()
            .user(user)
            .amount(amount)
            .status(Payment.PaymentStatus.PENDING)
            .gateway(gateway)
            .gatewayTransactionId(UUID.randomUUID().toString())
            .description("Ticket purchase")
            .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Simulate payment processing
        simulatePaymentGateway(savedPayment);
        
        logAudit(userId, "PAYMENT_PROCESSED", "PAYMENT", savedPayment.getId(), null);
        
        return savedPayment;
    }
    
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
    
    public void refundPayment(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        if (!payment.getStatus().equals(Payment.PaymentStatus.COMPLETED)) {
            throw new BadRequestException("Only completed payments can be refunded");
        }
        
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        
        logAudit(userId, "PAYMENT_REFUNDED", "PAYMENT", paymentId, null);
    }
    
    private void simulatePaymentGateway(Payment payment) {
        // Simulate successful payment (90% success rate)
        if (Math.random() > 0.1) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Payment declined by gateway");
        }
        paymentRepository.save(payment);
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
