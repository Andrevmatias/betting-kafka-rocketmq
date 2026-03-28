package com.betting.messaging.consumer;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.service.BetService;

@ExtendWith(MockitoExtension.class)
class EventOutcomeConsumerTest {

	@Mock
	private BetService betService;
	@InjectMocks
	private EventOutcomeConsumer consumer;

	@Test
	void handleEventOutcome_delegatesToBetService() {
		EventOutcomeMessage message = EventOutcomeMessage.builder().eventId(10L).winnerId(5L).build();

		consumer.handleEventOutcome(message);

		verify(betService).publishBetResults(10L, 5L);
	}
}
