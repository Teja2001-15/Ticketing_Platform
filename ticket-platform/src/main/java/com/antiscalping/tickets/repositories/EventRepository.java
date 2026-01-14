package com.antiscalping.tickets.repositories;

import com.antiscalping.tickets.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    
    @Query("SELECT e FROM Event e WHERE e.eventDate > :date ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(LocalDateTime date);
    
    @Query("SELECT e FROM Event e WHERE e.venue LIKE %:venue% AND e.status = 'ACTIVE'")
    List<Event> findByVenue(String venue);
}
