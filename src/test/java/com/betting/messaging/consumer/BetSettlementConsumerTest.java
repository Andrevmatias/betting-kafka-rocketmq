package com.betting.messaging.consumer;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betting.messaging.model.BetSettlementMessage;
import com.betting.service.BetSettlementService;

@ExtendWith(MockitoExtension.class)
class BetSettlementConsumerTest {

	@Mock
	private BetSettlementService betSettlementService;
	@InjectMocks
	private BetSettlementConsumer consumer;

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void onMessage_delegatesSettlementToService(boolean isWon) {
		BetSettlementMessage message = BetSettlementMessage.builder().betId(42L).won(isWon).build();

		consumer.onMessage(message);

		verify(betSettlementService).settleBet(42L, isWon);
	}
}
