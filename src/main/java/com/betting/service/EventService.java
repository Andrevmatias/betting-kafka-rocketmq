package com.betting.service;

import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betting.data.model.Event;
import com.betting.data.repository.EventRepository;
import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.messaging.producer.EventOutcomeProducer;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

	private final EventRepository eventRepository;
	private final EventOutcomeProducer eventOutcomeProducer;

	public Event getEventById(Long id) {
		return eventRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
	}

	@Transactional
	public Event publishOutcome(Long eventId, Long winnerId) {
		log.debug("Publishing outcome for eventId={}, winnerId={}", eventId, winnerId);
		Event event = getEventById(eventId);

		if (event.getWinnerId() != null) {
			throw new IllegalStateException("Outcome already published for event: " + eventId);
		}

		event.setWinnerId(winnerId);
		Event saved = eventRepository.save(event);

		EventOutcomeMessage message = EventOutcomeMessage.builder()
				.eventId(saved.getId())
				.winnerId(saved.getWinnerId())
				.build();

		eventOutcomeProducer.sendEventOutcome(message);
		log.info("Event outcome published: eventId={}, winnerId={}", eventId, winnerId);

		return saved;
	}
}
