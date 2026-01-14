package com.antiscalping.tickets.controllers;

import com.antiscalping.tickets.dto.UserDto;
import com.antiscalping.tickets.dto.UserLoginDto;
import com.antiscalping.tickets.dto.UserRegistrationDto;
import com.antiscalping.tickets.dto.AuthResponseDto;
import com.antiscalping.tickets.dto.ApiResponseDto;
import com.antiscalping.tickets.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<UserDto>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserDto userDto = userService.register(registrationDto);
            ApiResponseDto<UserDto> response = ApiResponseDto.<UserDto>builder()
                .success(true)
                .message("User registered successfully")
                .data(userDto)
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration error", e);
            throw new RuntimeException(e);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<AuthResponseDto>> login(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            String token = userService.login(loginDto);
            UserDto userDto = userService.getUserByEmail(loginDto.getEmail());
            
            AuthResponseDto authResponse = AuthResponseDto.builder()
                .token(token)
                .type("Bearer")
                .user(userDto)
                .build();
            
            ApiResponseDto<AuthResponseDto> response = ApiResponseDto.<AuthResponseDto>builder()
                .success(true)
                .message("Login successful")
                .data(authResponse)
                .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login error", e);
            throw new RuntimeException(e);
        }
    }
}
