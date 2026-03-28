package com.betting.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.messaging.model.MessageTopics;
import com.betting.service.BetService;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeConsumer {

	private final BetService betService;

	@KafkaListener(topics = MessageTopics.EVENT_OUTCOMES, groupId = "betting-group", concurrency = "6")
	public void handleEventOutcome(EventOutcomeMessage message) {
		log.info("Received event outcome from Kafka: eventId={}, winnerId={}", message.getEventId(), message.getWinnerId());

		betService.publishBetResults(message.getEventId(), message.getWinnerId());
	}
}
