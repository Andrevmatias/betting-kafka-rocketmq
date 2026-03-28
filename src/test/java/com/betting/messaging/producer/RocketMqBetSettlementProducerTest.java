package com.betting.messaging.producer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import org.apache.rocketmq.client.producer.SendCallback;
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

		doAnswer(invocation -> {
			SendCallback callback = invocation.getArgument(2);
			callback.onSuccess(null);
			return null;
		}).when(rocketMQTemplate).asyncSend(anyString(), (Object) any(), any(SendCallback.class));

		producer.sendBetSettlement(message);

		verify(rocketMQTemplate).asyncSend(eq(MessageTopics.BET_SETTLEMENTS), eq(message), any(SendCallback.class));
	}

	@Test
	void sendBetSettlement_templateThrows_throwsBrokerException() {
		BetSettlementMessage message = BetSettlementMessage.builder().betId(1L).won(true).build();

		doAnswer(invocation -> {
			SendCallback callback = invocation.getArgument(2);
			callback.onException(new RuntimeException("broker unavailable"));
			return null;
		}).when(rocketMQTemplate).asyncSend(anyString(), (Object) any(), any(SendCallback.class));

		assertThatThrownBy(() -> producer.sendBetSettlement(message))
				.isInstanceOf(BrokerException.class)
				.hasMessage("Failed to send bet settlement message")
				.hasCauseInstanceOf(RuntimeException.class);
	}
}
