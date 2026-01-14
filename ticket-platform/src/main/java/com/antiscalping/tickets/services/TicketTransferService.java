package com.antiscalping.tickets.services;

import com.antiscalping.tickets.dto.TicketTransferDto;
import com.antiscalping.tickets.entities.*;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
public class TicketTransferService {
    
    @Autowired
    private TicketTransferRepository ticketTransferRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TrustedCircleRepository trustedCircleRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public TicketTransfer initiateTransfer(Long fromUserId, TicketTransferDto transferDto) {
        User fromUser = userRepository.findById(fromUserId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        User toUser = userRepository.findById(transferDto.getRecipientId())
            .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));
        
        Ticket ticket = ticketRepository.findById(transferDto.getTicketId())
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        
        if (!ticket.getUser().getId().equals(fromUserId)) {
            throw new BadRequestException("You are not the owner of this ticket");
        }
        
        if (!ticket.getStatus().equals(Ticket.TicketStatus.AVAILABLE)) {
            throw new BadRequestException("Ticket is not available for transfer");
        }
        
        // Validate transfer type
        TicketTransfer.TransferType transferType = validateTransferType(fromUserId, transferDto.getTransferType(), toUser);
        
        TicketTransfer transfer = TicketTransfer.builder()
            .ticket(ticket)
            .fromUser(fromUser)
            .toUser(toUser)
            .transferType(transferType)
            .transferPrice(transferDto.getTransferPrice())
            .transferNotes(transferDto.getNotes())
            .status(TicketTransfer.TransferStatus.PENDING)
            .build();
        
        TicketTransfer savedTransfer = ticketTransferRepository.save(transfer);
        
        logAudit(fromUserId, "TRANSFER_INITIATED", "TICKET_TRANSFER", savedTransfer.getId(), null);
        
        return savedTransfer;
    }
    
    public void approveTransfer(Long transferId, Long approvingUserId) {
        TicketTransfer transfer = ticketTransferRepository.findById(transferId)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));
        
        if (!transfer.getToUser().getId().equals(approvingUserId)) {
            throw new BadRequestException("Only the recipient can approve this transfer");
        }
        
        if (!transfer.getStatus().equals(TicketTransfer.TransferStatus.PENDING)) {
            throw new BadRequestException("Transfer cannot be approved in current status");
        }
        
        transfer.setStatus(TicketTransfer.TransferStatus.APPROVED);
        transfer.setApprovedAt(LocalDateTime.now());
        ticketTransferRepository.save(transfer);
        
        logAudit(approvingUserId, "TRANSFER_APPROVED", "TICKET_TRANSFER", transferId, null);
    }
    
    public void completeTransfer(Long transferId) {
        TicketTransfer transfer = ticketTransferRepository.findById(transferId)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));
        
        if (!transfer.getStatus().equals(TicketTransfer.TransferStatus.APPROVED)) {
            throw new BadRequestException("Transfer must be approved before completion");
        }
        
        Ticket ticket = transfer.getTicket();
        ticket.setUser(transfer.getToUser());
        ticket.setStatus(Ticket.TicketStatus.TRANSFERRED);
        ticket.setTransferCount(ticket.getTransferCount() + 1);
        ticket.setTransferredAt(LocalDateTime.now());
        ticket.setTransferredFrom(transfer.getFromUser().getEmail());
        ticketRepository.save(ticket);
        
        transfer.setStatus(TicketTransfer.TransferStatus.COMPLETED);
        transfer.setCompletedAt(LocalDateTime.now());
        ticketTransferRepository.save(transfer);
        
        logAudit(transfer.getToUser().getId(), "TRANSFER_COMPLETED", "TICKET_TRANSFER", transferId, null);
    }
    
    public void rejectTransfer(Long transferId, Long rejectingUserId) {
        TicketTransfer transfer = ticketTransferRepository.findById(transferId)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer not found"));
        
        if (!transfer.getToUser().getId().equals(rejectingUserId)) {
            throw new BadRequestException("Only the recipient can reject this transfer");
        }
        
        transfer.setStatus(TicketTransfer.TransferStatus.REJECTED);
        ticketTransferRepository.save(transfer);
        
        logAudit(rejectingUserId, "TRANSFER_REJECTED", "TICKET_TRANSFER", transferId, null);
    }
    
    private TicketTransfer.TransferType validateTransferType(Long fromUserId, String transferType, User toUser) {
        if ("TRUSTED_CIRCLE".equalsIgnoreCase(transferType)) {
            if (!trustedCircleRepository.existsByUserIdAndTrustedUserId(fromUserId, toUser.getId())) {
                throw new BadRequestException("User is not in your trusted circle");
            }
            return TicketTransfer.TransferType.TRUSTED_CIRCLE;
        }
        
        if ("CONTROLLED_TRANSFER".equalsIgnoreCase(transferType)) {
            return TicketTransfer.TransferType.CONTROLLED_TRANSFER;
        }
        
        throw new BadRequestException("Invalid transfer type");
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
