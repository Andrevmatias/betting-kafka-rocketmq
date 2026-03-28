package com.betting.messaging.consumer;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.model.EventOutcomeMessage;
import com.betting.messaging.model.MessageTopics;
import com.betting.messaging.producer.BetSettlementProducer;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeConsumer {

	private final BetRepository betRepository;
	private final BetSettlementProducer betSettlementProducer;

	@KafkaListener(topics = MessageTopics.EVENT_OUTCOMES, groupId = "betting-group")
	public void handleEventOutcome(EventOutcomeMessage message) {
		log.info("Received event outcome from Kafka: eventId={}, winnerId={}", message.getEventId(), message.getWinnerId());

		List<Bet> bets = betRepository.findByEventId(message.getEventId());
		log.debug("Found {} bets for eventId={}", bets.size(), message.getEventId());

		for (Bet bet : bets) {
			if (bet.getStatus() != BetStatus.PENDING) {
				log.debug("Skipping bet id={} with non-PENDING status={}", bet.getId(), bet.getStatus());
				continue;
			}

			boolean won = bet.getEventWinnerId().equals(message.getWinnerId());
			BetStatus newStatus = won ? BetStatus.WON : BetStatus.LOST;

			bet.setStatus(newStatus);
			betRepository.save(bet);

			BetSettlementMessage settlementMessage = BetSettlementMessage.builder()
					.betId(bet.getId())
					.userId(bet.getUserId())
					.eventId(bet.getEventId())
					.amount(bet.getAmount())
					.won(won)
					.betWinnerId(bet.getEventWinnerId())
					.eventWinnerId(message.getWinnerId())
					.build();

			log.info("Sending bet settlement to RocketMQ: betId={}, userId={}, won={}", bet.getId(), bet.getUserId(), won);
			betSettlementProducer.sendBetSettlement(settlementMessage);
		}
	}
}
