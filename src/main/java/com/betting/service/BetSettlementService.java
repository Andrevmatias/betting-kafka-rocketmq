package com.betting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetSettlementService {

	private final BetRepository betRepository;

	@Transactional
	public void settleBet(Long betId, boolean isWon) {
		BetStatus newStatus = isWon ? BetStatus.WON : BetStatus.LOST;

		int updated = betRepository.settleIfPending(betId, newStatus);

		if (updated == 0) {
			log.debug("Skipping bet id={}, not in PENDING status", betId);
		}
	}
}
