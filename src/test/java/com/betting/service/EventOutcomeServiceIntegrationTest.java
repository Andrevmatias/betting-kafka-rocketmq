package com.betting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.betting.data.model.Event;
import com.betting.data.repository.EventRepository;
import com.betting.messaging.producer.EventOutcomeProducer;

@DataJpaTest
class EventOutcomeServiceIntegrationTest {

	@Autowired
	private EventRepository eventRepository;

	private EventOutcomeProducer eventOutcomeProducer;
	private EventOutcomeService eventOutcomeService;

	@BeforeEach
	void setUp() {
		eventOutcomeProducer = mock(EventOutcomeProducer.class);
		eventOutcomeService = new EventOutcomeService(eventRepository, eventOutcomeProducer);
	}

	@Test
	void publishOutcome_eventNotFound_createsEventWithWinnerAndPublishes() {
		eventOutcomeService.publishOutcome(999L, 5L);

		Event saved = eventRepository.findById(999L).orElseThrow();
		assertThat(saved.getWinnerId()).isEqualTo(5L);
		verify(eventOutcomeProducer).sendEventOutcome(argThat(m ->
				m.getEventId().equals(999L)
						&& m.getWinnerId().equals(5L))
		);
	}

	@Test
	void publishOutcome_eventExistsWithNoWinner_setsWinnerAndPublishes() {
		Event event = eventRepository.save(Event.builder().id(1L).build());

		eventOutcomeService.publishOutcome(event.getId(), 3L);

		assertThat(eventRepository.findById(event.getId()).orElseThrow().getWinnerId()).isEqualTo(3L);
		verify(eventOutcomeProducer).sendEventOutcome(argThat(m ->
				m.getEventId().equals(1L)
						&& m.getWinnerId().equals(3L))
		);
	}

	@Test
	void publishOutcome_differentWinnerAlreadySet_throwsIllegalStateException() {
		Event event = eventRepository.save(Event.builder().id(1L).winnerId(2L).build());

		assertThatThrownBy(() -> eventOutcomeService.publishOutcome(event.getId(), 3L))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining(event.getId().toString());
		
		verifyNoInteractions(eventOutcomeProducer);
	}

	@Test
	void publishOutcome_sameWinnerAlreadySet_republishesWithoutError() {
		Event event = eventRepository.save(Event.builder().id(1L).winnerId(2L).build());

		eventOutcomeService.publishOutcome(event.getId(), 2L);

		verify(eventOutcomeProducer).sendEventOutcome(argThat(m ->
				m.getEventId().equals(1L)
						&& m.getWinnerId().equals(2L))
		);
	}
}
