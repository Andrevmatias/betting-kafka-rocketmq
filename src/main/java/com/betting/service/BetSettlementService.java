package com.betting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetSettlementService {

	private final BetRepository betRepository;

	@Transactional
	public void settleBet(Long betId, Boolean isWon) {
		Bet bet = betRepository.getReferenceById(betId);

		if (bet.getStatus() != BetStatus.PENDING) {
			log.debug("Skipping bet id={} with non-PENDING status={}", bet.getId(), bet.getStatus());
		} else {
			BetStatus newStatus = Boolean.TRUE.equals(isWon) ? BetStatus.WON : BetStatus.LOST;
			bet.setStatus(newStatus);
			betRepository.save(bet);
		}
	}
}
