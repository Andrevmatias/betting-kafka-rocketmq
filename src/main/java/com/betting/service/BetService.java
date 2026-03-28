package com.betting.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.producer.BetSettlementProducer;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetService {

	private final BetRepository betRepository;
	private final BetSettlementProducer betSettlementProducer;

	public List<Bet> getAllBets() {
		return betRepository.findAll();
	}

	public void publishBetResults(Long eventId, Long winnerId) {
		List<Bet> pendingBets = betRepository.findByEventIdAndStatus(eventId, BetStatus.PENDING);

		log.debug("Found {} pending bets for eventId={}", pendingBets.size(), eventId);

		for (Bet bet : pendingBets) {
			boolean won = bet.getEventWinnerId().equals(winnerId);

			BetSettlementMessage settlementMessage = BetSettlementMessage.builder()
					.betId(bet.getId())
					.won(won)
					.build();

			betSettlementProducer.sendBetSettlement(settlementMessage);
		}
	}
}
