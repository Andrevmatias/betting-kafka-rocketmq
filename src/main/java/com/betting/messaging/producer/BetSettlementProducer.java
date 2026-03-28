package com.betting.messaging.producer;

import com.betting.messaging.model.BetSettlementMessage;

public interface BetSettlementProducer {
	void sendBetSettlement(BetSettlementMessage message);
}
