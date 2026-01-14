package com.antiscalping.tickets.services;

import com.antiscalping.tickets.dto.UserDto;
import com.antiscalping.tickets.dto.UserLoginDto;
import com.antiscalping.tickets.dto.UserRegistrationDto;
import com.antiscalping.tickets.entities.User;
import com.antiscalping.tickets.entities.AuditLog;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.UserRepository;
import com.antiscalping.tickets.repositories.AuditLogRepository;
import com.antiscalping.tickets.utils.JwtTokenProvider;
import com.antiscalping.tickets.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.security.NoSuchAlgorithmException;

@Service
@Transactional
@Slf4j
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private SecurityUtils securityUtils;
    
    public UserDto register(UserRegistrationDto registrationDto) throws NoSuchAlgorithmException {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        User user = User.builder()
            .email(registrationDto.getEmail())
            .passwordHash(securityUtils.hashPassword(registrationDto.getPassword()))
            .firstName(registrationDto.getFirstName())
            .lastName(registrationDto.getLastName())
            .phone(registrationDto.getPhone())
            .status(User.UserStatus.ACTIVE)
            .build();
        
        User savedUser = userRepository.save(user);
        
        logAudit(savedUser.getId(), "USER_REGISTERED", "USER", savedUser.getId(), null);
        
        return mapToDto(savedUser);
    }
    
    public String login(UserLoginDto loginDto) throws NoSuchAlgorithmException {
        User user = userRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!securityUtils.verifyPassword(loginDto.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }
        
        if (!user.getStatus().equals(User.UserStatus.ACTIVE)) {
            throw new BadRequestException("User account is inactive");
        }
        
        logAudit(user.getId(), "USER_LOGIN", "USER", user.getId(), null);
        
        return tokenProvider.generateToken(user.getId().toString());
    }
    
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }
    
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToDto(user);
    }
    
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getPhone() != null) {
            user.setPhone(userDto.getPhone());
        }
        
        User updatedUser = userRepository.save(user);
        
        logAudit(userId, "USER_UPDATED", "USER", userId, null);
        
        return mapToDto(updatedUser);
    }
    
    public void suspendUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        user.setStatus(User.UserStatus.SUSPENDED);
        userRepository.save(user);
        
        logAudit(userId, "USER_SUSPENDED", "USER", userId, reason);
    }
    
    private UserDto mapToDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .phone(user.getPhone())
            .status(user.getStatus().toString())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
    
    private void logAudit(Long userId, String action, String entityType, Long entityId, String details) {
        AuditLog auditLog = AuditLog.builder()
            .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
            .action(action)
            .entityType(entityType)
            .entityId(entityId)
            .details(details)
            .build();
        auditLogRepository.save(auditLog);
    }
}
