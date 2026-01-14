package com.antiscalping.tickets.controllers;

import com.antiscalping.tickets.dto.EventCreateDto;
import com.antiscalping.tickets.dto.EventDto;
import com.antiscalping.tickets.dto.ApiResponseDto;
import com.antiscalping.tickets.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
@Slf4j
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @PostMapping
    public ResponseEntity<ApiResponseDto<EventDto>> createEvent(@Valid @RequestBody EventCreateDto eventCreateDto) {
        try {
            Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            EventDto eventDto = eventService.createEvent(eventCreateDto, userId);
            
            ApiResponseDto<EventDto> response = ApiResponseDto.<EventDto>builder()
                .success(true)
                .message("Event created successfully")
                .data(eventDto)
                .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Event creation error", e);
            throw new RuntimeException(e);
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponseDto<List<EventDto>>> getAllEvents() {
        List<EventDto> events = eventService.getAllEvents();
        ApiResponseDto<List<EventDto>> response = ApiResponseDto.<List<EventDto>>builder()
            .success(true)
            .message("Events retrieved successfully")
            .data(events)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponseDto<List<EventDto>>> getUpcomingEvents() {
        List<EventDto> events = eventService.getUpcomingEvents();
        ApiResponseDto<List<EventDto>> response = ApiResponseDto.<List<EventDto>>builder()
            .success(true)
            .message("Upcoming events retrieved successfully")
            .data(events)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponseDto<EventDto>> getEventById(@PathVariable Long eventId) {
        EventDto eventDto = eventService.getEventById(eventId);
        ApiResponseDto<EventDto> response = ApiResponseDto.<EventDto>builder()
            .success(true)
            .message("Event retrieved successfully")
            .data(eventDto)
            .build();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search/venue")
    public ResponseEntity<ApiResponseDto<List<EventDto>>> searchByVenue(@RequestParam String venue) {
        List<EventDto> events = eventService.searchEventsByVenue(venue);
        ApiResponseDto<List<EventDto>> response = ApiResponseDto.<List<EventDto>>builder()
            .success(true)
            .message("Events found")
            .data(events)
            .build();
        return ResponseEntity.ok(response);
    }
}
