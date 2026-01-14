package com.antiscalping.tickets.services;

import com.antiscalping.tickets.entities.AuditLog;
import com.antiscalping.tickets.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Service
@Transactional
@Slf4j
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public List<AuditLog> getUserAuditLogs(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLog> getActionLogs(String action) {
        return auditLogRepository.findByAction(action);
    }
    
    public List<AuditLog> getEntityLogs(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
