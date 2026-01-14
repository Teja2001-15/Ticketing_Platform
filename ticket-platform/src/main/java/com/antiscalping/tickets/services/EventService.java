package com.antiscalping.tickets.services;

import com.antiscalping.tickets.dto.EventDto;
import com.antiscalping.tickets.dto.EventCreateDto;
import com.antiscalping.tickets.entities.Event;
import com.antiscalping.tickets.entities.AuditLog;
import com.antiscalping.tickets.exceptions.BadRequestException;
import com.antiscalping.tickets.exceptions.ResourceNotFoundException;
import com.antiscalping.tickets.repositories.EventRepository;
import com.antiscalping.tickets.repositories.TicketRepository;
import com.antiscalping.tickets.repositories.AuditLogRepository;
import com.antiscalping.tickets.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public EventDto createEvent(EventCreateDto eventCreateDto, Long userId) {
        LocalDateTime eventDate = LocalDateTime.parse(eventCreateDto.getEventDate(), formatter);
        
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Event date must be in the future");
        }
        
        Event event = Event.builder()
            .name(eventCreateDto.getName())
            .description(eventCreateDto.getDescription())
            .eventDate(eventDate)
            .venue(eventCreateDto.getVenue())
            .totalCapacity(eventCreateDto.getTotalCapacity())
            .availableTickets(eventCreateDto.getTotalCapacity())
            .ticketPrice(eventCreateDto.getTicketPrice())
            .organizerId(userId.toString())
            .build();
        
        Event savedEvent = eventRepository.save(event);
        
        logAudit(userId, "EVENT_CREATED", "EVENT", savedEvent.getId(), null);
        
        return mapToDto(savedEvent);
    }
    
    public EventDto getEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return mapToDto(event);
    }
    
    public List<EventDto> getAllEvents() {
        List<Event> events = eventRepository.findByStatus("ACTIVE");
        return events.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<EventDto> getUpcomingEvents() {
        List<Event> events = eventRepository.findUpcomingEvents(LocalDateTime.now());
        return events.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public List<EventDto> searchEventsByVenue(String venue) {
        List<Event> events = eventRepository.findByVenue(venue);
        return events.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    public void updateAvailableTickets(Long eventId, Integer quantity) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        event.setAvailableTickets(event.getAvailableTickets() - quantity);
        
        if (event.getAvailableTickets() < 0) {
            event.setAvailableTickets(0);
        }
        
        eventRepository.save(event);
    }
    
    public void cancelEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        
        event.setStatus(Event.EventStatus.CANCELLED);
        eventRepository.save(event);
        
        logAudit(userId, "EVENT_CANCELLED", "EVENT", eventId, null);
    }
    
    private EventDto mapToDto(Event event) {
        long totalTickets = ticketRepository.countByEventId(event.getId());
        long soldTickets = ticketRepository.countByEventIdAndStatus(event.getId(), "AVAILABLE");
        
        return EventDto.builder()
            .id(event.getId())
            .name(event.getName())
            .description(event.getDescription())
            .eventDate(event.getEventDate())
            .venue(event.getVenue())
            .totalCapacity(event.getTotalCapacity())
            .availableTickets(event.getAvailableTickets())
            .ticketPrice(event.getTicketPrice())
            .status(event.getStatus().toString())
            .createdAt(event.getCreatedAt())
            .updatedAt(event.getUpdatedAt())
            .build();
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
