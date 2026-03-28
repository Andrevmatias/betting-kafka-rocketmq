package com.betting.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.betting.exception.BrokerException;
import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.messaging.model.MessageTopics;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventOutcomeProducer implements EventOutcomeProducer {

	private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;

	@Override
	public void sendEventOutcome(EventOutcomeMessage message) {
		log.info("Sending event outcome to Kafka topic={}: eventId={}, winnerId={}", MessageTopics.EVENT_OUTCOMES, message.getEventId(), message.getWinnerId());
		try {
			SendResult<String, EventOutcomeMessage> result = kafkaTemplate
					.send(MessageTopics.EVENT_OUTCOMES, String.valueOf(message.getEventId()), message)
					.get();
			log.info("Event outcome message sent successfully for eventId={}, offset={}",
					message.getEventId(),
					result.getRecordMetadata().offset());
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		} catch (Exception ex) {
			throw new BrokerException("Failed to send event outcome for eventId=" + message.getEventId(), ex);
		}
	}
}
