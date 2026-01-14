package com.antiscalping.tickets.services;

import com.antiscalping.tickets.entities.User;
import com.antiscalping.tickets.entities.TrustedCircle;
import com.antiscalping.tickets.entities.AuditLog;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.UserRepository;
import com.antiscalping.tickets.repositories.TrustedCircleRepository;
import com.antiscalping.tickets.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TrustedCircleService {
    
    @Autowired
    private TrustedCircleRepository trustedCircleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public TrustedCircle addTrustedUser(Long userId, Long trustedUserId, String relationship) {
        if (userId.equals(trustedUserId)) {
            throw new BadRequestException("Cannot add yourself to trusted circle");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User trustedUser = userRepository.findById(trustedUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Trusted user not found"));
        
        if (trustedCircleRepository.existsByUserIdAndTrustedUserId(userId, trustedUserId)) {
            throw new BadRequestException("User is already in trusted circle");
        }
        
        TrustedCircle trustedCircle = TrustedCircle.builder()
            .user(user)
            .trustedUser(trustedUser)
            .relationship(relationship != null ? relationship : "FRIEND")
            .build();
        
        TrustedCircle savedCircle = trustedCircleRepository.save(trustedCircle);
        
        logAudit(userId, "TRUSTED_USER_ADDED", "TRUSTED_CIRCLE", savedCircle.getId(), null);
        
        return savedCircle;
    }
    
    public void removeTrustedUser(Long userId, Long trustedUserId) {
        TrustedCircle trustedCircle = trustedCircleRepository.findByUserIdAndTrustedUserId(userId, trustedUserId)
            .orElseThrow(() -> new ResourceNotFoundException("Trusted user not found in circle"));
        
        trustedCircleRepository.delete(trustedCircle);
        
        logAudit(userId, "TRUSTED_USER_REMOVED", "TRUSTED_CIRCLE", trustedCircle.getId(), null);
    }
    
    public List<TrustedCircle> getTrustedCircle(Long userId) {
        return trustedCircleRepository.findByUserId(userId);
    }
    
    public boolean isTrusted(Long userId, Long trustedUserId) {
        return trustedCircleRepository.existsByUserIdAndTrustedUserId(userId, trustedUserId);
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
