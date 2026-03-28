package com.betting.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.model.MessageTopics;
import com.betting.service.BetSettlementService;

@Component
@RocketMQMessageListener(
		topic = MessageTopics.BET_SETTLEMENTS,
		consumerGroup = "bet-settlement-consumer-group"
)
@RequiredArgsConstructor
@Slf4j
public class BetSettlementConsumer implements RocketMQListener<BetSettlementMessage> {

	private final BetSettlementService betSettlementService;

	@Override
	public void onMessage(BetSettlementMessage message) {
		log.info("Received bet settlement from RocketMQ: betId={}, won={}", message.getBetId(), message.isWon());

		betSettlementService.settleBet(message.getBetId(), message.isWon());
	}
}
