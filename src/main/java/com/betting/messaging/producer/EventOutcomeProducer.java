package com.betting.messaging.producer;

import com.betting.messaging.model.EventOutcomeMessage;

public interface EventOutcomeProducer {
	void sendEventOutcome(EventOutcomeMessage message);
}
