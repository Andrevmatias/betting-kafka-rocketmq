package com.betting.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.model.MessageTopics;

@Component
@RocketMQMessageListener(
		topic = MessageTopics.BET_SETTLEMENTS,
		consumerGroup = "bet-settlement-consumer-group"
)
@RequiredArgsConstructor
@Slf4j
public class BetSettlementConsumer implements RocketMQListener<BetSettlementMessage> {

	private final BetRepository betRepository;

	@Override
	@Transactional
	public void onMessage(BetSettlementMessage message) {
		log.info("Received bet settlement from RocketMQ: betId={}, userId={}, won={}", message.getBetId(), message.getUserId(), message.isWon());

		try {
			Long betId = message.getBetId();
			Bet bet = betRepository.findById(betId)
					.orElseThrow(() -> new IllegalArgumentException("Bet not found with id: " + message.getBetId()));

			if (bet.getStatus() == BetStatus.SETTLED) {
				log.warn("Bet id={} is already SETTLED, skipping", betId);
				return;
			}

			bet.setStatus(BetStatus.SETTLED);
			betRepository.save(bet);

			log.info("Bet id={} marked as SETTLED. userId={}, eventId={}, amount={}, won={}",
					betId, message.getUserId(), message.getEventId(), message.getAmount(), message.isWon());

		} catch (Exception e) {
			log.error("Error settling bet id={}: {}", message.getBetId(), e.getMessage(), e);
			throw e;
		}
	}
}
