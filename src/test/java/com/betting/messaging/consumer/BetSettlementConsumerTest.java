package com.betting.messaging.consumer;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

	@Test
	void onMessage_delegatesSettlementToService() {
		BetSettlementMessage message = BetSettlementMessage.builder().betId(42L).won(true).build();

		consumer.onMessage(message);

		verify(betSettlementService).settleBet(42L, true);
	}

	@Test
	void onMessage_lostBet_delegatesWithWonFalse() {
		BetSettlementMessage message = BetSettlementMessage.builder().betId(7L).won(false).build();

		consumer.onMessage(message);

		verify(betSettlementService).settleBet(7L, false);
	}
}
