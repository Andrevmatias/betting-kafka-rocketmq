package com.betting.service;

import com.betting.dto.EventOutcomeMessage;
import com.betting.dto.EventRequest;
import com.betting.kafka.EventOutcomeProducer;
import com.betting.model.Event;
import com.betting.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final EventOutcomeProducer eventOutcomeProducer;

    @Transactional
    public Event createEvent(EventRequest request) {
        log.debug("Creating event with name={}", request.getName());
        Event event = Event.builder()
                .name(request.getName())
                .build();
        Event saved = eventRepository.save(event);
        log.info("Event created successfully: id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
    }

    @Transactional
    public Event publishOutcome(UUID eventId, String winnerId) {
        log.debug("Publishing outcome for eventId={}, winnerId={}", eventId, winnerId);
        Event event = getEventById(eventId);

        if (event.getWinnerId() != null) {
            throw new IllegalStateException("Outcome already published for event: " + eventId);
        }

        event.setWinnerId(winnerId);
        Event saved = eventRepository.save(event);

        EventOutcomeMessage message = EventOutcomeMessage.builder()
                .eventId(saved.getId().toString())
                .eventName(saved.getName())
                .winnerId(saved.getWinnerId())
                .build();

        eventOutcomeProducer.sendEventOutcome(message);
        log.info("Event outcome published: eventId={}, winnerId={}", eventId, winnerId);

        return saved;
    }
}
