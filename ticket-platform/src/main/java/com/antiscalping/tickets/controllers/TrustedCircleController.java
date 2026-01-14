package com.antiscalping.tickets.controllers;

import com.antiscalping.tickets.dto.ApiResponseDto;
import com.antiscalping.tickets.entities.TrustedCircle;
import com.antiscalping.tickets.services.TrustedCircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/trusted-circle")
@CrossOrigin(origins = "*")
@Slf4j
public class TrustedCircleController {
    
    @Autowired
    private TrustedCircleService trustedCircleService;
    
    @PostMapping("/add/{trustedUserId}")
    public ResponseEntity<ApiResponseDto<String>> addTrustedUser(
            @PathVariable Long trustedUserId,
            @RequestParam(required = false) String relationship) {
        try {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            trustedCircleService.addTrustedUser(userId, trustedUserId, relationship);
            
            ApiResponseDto<String> response = ApiResponseDto.<String>builder()
                .success(true)
                .message("User added to trusted circle successfully")
                .data("User can now receive transfers from you")
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Add trusted user error", e);
            throw new RuntimeException(e);
        }
    }
    
    @DeleteMapping("/remove/{trustedUserId}")
    public ResponseEntity<ApiResponseDto<String>> removeTrustedUser(@PathVariable Long trustedUserId) {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        trustedCircleService.removeTrustedUser(userId, trustedUserId);
        
        ApiResponseDto<String> response = ApiResponseDto.<String>builder()
            .success(true)
            .message("User removed from trusted circle successfully")
            .data("User can no longer receive transfers from you")
            .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<TrustedCircle>>> getTrustedCircle() {
        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        List<TrustedCircle> trustedCircles = trustedCircleService.getTrustedCircle(userId);
        
        ApiResponseDto<List<TrustedCircle>> response = ApiResponseDto.<List<TrustedCircle>>builder()
            .success(true)
            .message("Trusted circle retrieved successfully")
            .data(trustedCircles)
            .build();
        return ResponseEntity.ok(response);
    }
}
