package com.antiscalping.tickets.repositories;

import com.antiscalping.tickets.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserId(Long userId);
    
    List<Payment> findByUserIdAndStatus(Long userId, String status);
    
    List<Payment> findByStatus(String status);
    
    List<Payment> findByGatewayTransactionId(String transactionId);
}
