package com.betting.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
class BetSettlementServiceIntegrationTest {

	@Autowired
	private BetRepository betRepository;

	@Autowired
	private EntityManager entityManager;

	private BetSettlementService betSettlementService;

	@BeforeEach
	void setUp() {
		betSettlementService = new BetSettlementService(betRepository);
	}

	@Test
	void settleBet_pendingBetWon_statusBecomesWon() {
		Bet bet = betRepository.save(pendingBet());

		betSettlementService.settleBet(bet.getId(), true);

		entityManager.refresh(bet);

		assertThat(betRepository.findById(bet.getId()).orElseThrow().getStatus()).isEqualTo(BetStatus.WON);
	}

	@Test
	void settleBet_pendingBetLost_statusBecomesLost() {
		Bet bet = betRepository.save(pendingBet());

		betSettlementService.settleBet(bet.getId(), false);

		entityManager.refresh(bet);

		assertThat(betRepository.findById(bet.getId()).orElseThrow().getStatus()).isEqualTo(BetStatus.LOST);
	}

	@Test
	void settleBet_alreadySettled_statusRemainsUnchanged() {
		Bet bet = betRepository.save(
				Bet.builder()
						.userId(1L)
						.eventId(1L)
						.eventMarketId(1L)
						.eventWinnerId(1L)
						.amount(BigDecimal.TEN)
						.status(BetStatus.WON)
						.build()
		);

		betSettlementService.settleBet(bet.getId(), false);

		assertThat(betRepository.findById(bet.getId()).orElseThrow().getStatus()).isEqualTo(BetStatus.WON);
	}

	private Bet pendingBet() {
		return Bet.builder()
				.userId(1L)
				.eventId(1L)
				.eventMarketId(1L)
				.eventWinnerId(1L)
				.amount(BigDecimal.TEN)
				.build();
	}
}
