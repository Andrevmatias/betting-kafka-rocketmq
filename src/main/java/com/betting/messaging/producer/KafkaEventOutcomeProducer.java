package com.betting.messaging.producer;

import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.messaging.model.MessageTopics;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventOutcomeProducer implements EventOutcomeProducer {

	private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;

	@Override public void sendEventOutcome(EventOutcomeMessage message) {
		log.info("Sending event outcome to Kafka topic={}: eventId={}, winnerId={}", MessageTopics.EVENT_OUTCOMES, message.getEventId(), message.getWinnerId());
		CompletableFuture<SendResult<String, EventOutcomeMessage>> future =
				kafkaTemplate.send(MessageTopics.EVENT_OUTCOMES, message.getEventId().toString(), message);

		future.whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Failed to send event outcome message for eventId={}: {}", message.getEventId(), ex.getMessage(), ex);
			} else {
				log.info("Event outcome message sent successfully for eventId={}, offset={}",
						message.getEventId(),
						result.getRecordMetadata().offset());
			}
		});
	}
}
