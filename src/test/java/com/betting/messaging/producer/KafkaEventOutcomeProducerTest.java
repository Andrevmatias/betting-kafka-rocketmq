package com.betting.messaging.producer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.betting.exception.BrokerException;
import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.messaging.model.MessageTopics;

@ExtendWith(MockitoExtension.class)
class KafkaEventOutcomeProducerTest {

	@Mock
	private KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;
	@InjectMocks
	private KafkaEventOutcomeProducer producer;

	@Test
	@SuppressWarnings("unchecked")
	void sendEventOutcome_callsTemplateWithCorrectTopicAndKey() {
		EventOutcomeMessage message = EventOutcomeMessage.builder().eventId(10L).winnerId(5L).build();
		SendResult<String, EventOutcomeMessage> sendResult = mock(SendResult.class);

		when(sendResult.getRecordMetadata()).thenReturn(mock(org.apache.kafka.clients.producer.RecordMetadata.class));
		when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(sendResult));

		producer.sendEventOutcome(message);

		verify(kafkaTemplate).send(MessageTopics.EVENT_OUTCOMES, "10", message);
	}

	@Test
	void sendEventOutcome_brokerFailure_throwsBrokerException() {
		EventOutcomeMessage message = EventOutcomeMessage.builder().eventId(10L).winnerId(5L).build();
		when(kafkaTemplate.send(anyString(), anyString(), any()))
				.thenReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")));

		assertThatThrownBy(() -> producer.sendEventOutcome(message))
				.isInstanceOf(BrokerException.class)
				.hasMessage("Failed to send event outcome for eventId=10");
	}
}
