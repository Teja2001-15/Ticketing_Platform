package com.antiscalping.tickets.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import com.antiscalping.tickets.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ApiResponseDto<?> response = ApiResponseDto.builder()
            .success(false)
            .message(ex.getMessage())
            .error("RESOURCE_NOT_FOUND")
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseDto<?>> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        ApiResponseDto<?> response = ApiResponseDto.builder()
            .success(false)
            .message(ex.getMessage())
            .error("UNAUTHORIZED")
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDto<?>> handleBadRequest(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        ApiResponseDto<?> response = ApiResponseDto.builder()
            .success(false)
            .message(ex.getMessage())
            .error("BAD_REQUEST")
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(FraudDetectedException.class)
    public ResponseEntity<ApiResponseDto<?>> handleFraudDetected(FraudDetectedException ex) {
        log.warn("Fraud detected: {}", ex.getMessage());
        ApiResponseDto<?> response = ApiResponseDto.builder()
            .success(false)
            .message(ex.getMessage())
            .error("FRAUD_DETECTED")
            .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
        
        ApiResponseDto<?> response = ApiResponseDto.builder()
            .success(false)
            .message(message)
            .error("VALIDATION_ERROR")
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<?>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiResponseDto<?> response = ApiResponseDto.builder()
            .success(false)
            .message("An unexpected error occurred")
            .error("INTERNAL_SERVER_ERROR")
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
