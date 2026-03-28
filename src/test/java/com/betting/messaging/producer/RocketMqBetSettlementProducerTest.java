package com.betting.messaging.producer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betting.exception.BrokerException;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.model.MessageTopics;

@ExtendWith(MockitoExtension.class)
class RocketMqBetSettlementProducerTest {

	@Mock
	private RocketMQTemplate rocketMQTemplate;
	@InjectMocks
	private RocketMqBetSettlementProducer producer;

	@Test
	void sendBetSettlement_callsTemplateWithCorrectTopicAndMessage() {
		BetSettlementMessage message = BetSettlementMessage.builder().betId(1L).won(true).build();

		producer.sendBetSettlement(message);

		verify(rocketMQTemplate).convertAndSend(MessageTopics.BET_SETTLEMENTS, message);
	}

	@Test
	void sendBetSettlement_templateThrows_throwsBrokerException() {
		BetSettlementMessage message = BetSettlementMessage.builder().betId(1L).won(true).build();

		doThrow(new RuntimeException("broker unavailable")).when(rocketMQTemplate).convertAndSend(anyString(), (Object) any());

		assertThatThrownBy(() -> producer.sendBetSettlement(message))
				.isInstanceOf(BrokerException.class)
				.hasMessageContaining("Failed to send bet settlement message")
				.hasCauseInstanceOf(RuntimeException.class);
	}
}
