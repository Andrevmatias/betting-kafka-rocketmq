package com.betting.service;

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
public class EventOutcomeService {

	private final EventRepository eventRepository;
	private final EventOutcomeProducer eventOutcomeProducer;

	@Transactional
	public void publishOutcome(Long eventId, Long winnerId) {
		log.debug("Publishing outcome for eventId={}, winnerId={}", eventId, winnerId);

		Event event = getEventById(eventId);

		if (event.getWinnerId() != null && !event.getWinnerId().equals(winnerId)) {
			throw new IllegalStateException("Outcome already published for event: " + eventId);
		}

		event.setWinnerId(winnerId);
		eventRepository.save(event);

		EventOutcomeMessage message = EventOutcomeMessage.builder()
				.eventId(eventId)
				.winnerId(winnerId)
				.build();

		eventOutcomeProducer.sendEventOutcome(message);

		log.info("Event outcome published: eventId={}, winnerId={}", eventId, winnerId);
	}

	private Event getEventById(Long id) {
		return eventRepository.findById(id)
				.orElseGet(() -> {
					Event newEvent = Event.builder().id(id).build();
					return eventRepository.save(newEvent);
				});
	}
}
