package com.betting.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import com.betting.exception.BrokerException;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.model.MessageTopics;

@Component
@RequiredArgsConstructor
@Slf4j
public class RocketMqBetSettlementProducer implements BetSettlementProducer {

	private final RocketMQTemplate rocketMQTemplate;

	@Override public void sendBetSettlement(BetSettlementMessage message) {
		log.info("Sending bet settlement to RocketMQ topic={}: betId={}, won={}", MessageTopics.BET_SETTLEMENTS, message.getBetId(), message.isWon());
		try {
			rocketMQTemplate.convertAndSend(MessageTopics.BET_SETTLEMENTS, message);
			log.info("Bet settlement message sent successfully for betId={}", message.getBetId());
		} catch (Exception e) {
			log.error("Failed to send bet settlement message for betId={}: {}", message.getBetId(), e.getMessage(), e);
			throw new BrokerException("Failed to send bet settlement message", e);
		}
	}
}
