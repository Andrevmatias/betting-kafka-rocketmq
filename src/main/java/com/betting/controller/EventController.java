package com.betting.controller;

import com.betting.dto.EventRequest;
import com.betting.model.Event;
import com.betting.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequest request) {
        log.debug("POST /events - creating event with name={}", request.getName());
        Event event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        log.debug("GET /events - listing all events");
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable UUID id) {
        log.debug("GET /events/{} - getting event by id", id);
        try {
            return ResponseEntity.ok(eventService.getEventById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/outcome")
    public ResponseEntity<Event> publishOutcome(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        String winnerId = body.get("winnerId");
        if (winnerId == null || winnerId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        log.debug("POST /events/{}/outcome - publishing outcome with winnerId={}", id, winnerId);
        try {
            Event event = eventService.publishOutcome(id, winnerId);
            return ResponseEntity.ok(event);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
