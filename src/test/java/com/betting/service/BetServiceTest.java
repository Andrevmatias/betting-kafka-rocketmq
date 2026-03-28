package com.betting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.betting.data.model.Bet;
import com.betting.data.model.BetStatus;
import com.betting.data.repository.BetRepository;
import com.betting.mapper.BetMapper;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.producer.BetSettlementProducer;
import com.betting.service.model.BetDto;

@ExtendWith(MockitoExtension.class)
class BetServiceTest {

	@Mock
	private BetRepository betRepository;
	@Mock
	private BetSettlementProducer betSettlementProducer;
	@Mock
	private BetMapper betMapper;
	@InjectMocks
	private BetService betService;

	@Test
	void getAllBets_returnsMappedDtos() {
		Bet bet = Bet.builder().id(1L).build();
		BetDto betDto = BetDto.builder().id(1L).build();

		when(betRepository.findAll()).thenReturn(List.of(bet));
		when(betMapper.toDto(bet)).thenReturn(betDto);

		assertThat(betService.getAllBets()).containsExactly(betDto);
	}

	@Test
	void publishBetResults_sendsWonMessageWhenWinnerMatches() {
		Bet bet = Bet.builder()
				.id(1L)
				.eventId(10L)
				.eventWinnerId(5L)
				.amount(BigDecimal.TEN)
				.build();

		when(betRepository.findByEventIdAndStatus(10L, BetStatus.PENDING)).thenReturn(List.of(bet));

		betService.publishBetResults(10L, 5L);

		ArgumentCaptor<BetSettlementMessage> captor = forClass(BetSettlementMessage.class);
		verify(betSettlementProducer).sendBetSettlement(captor.capture());
		assertThat(captor.getValue().getBetId()).isEqualTo(1L);
		assertThat(captor.getValue().isWon()).isTrue();
	}

	@Test
	void publishBetResults_sendsLostMessageWhenWinnerDoesNotMatch() {
		Bet bet = Bet.builder()
				.id(2L)
				.eventId(10L)
				.eventWinnerId(5L)
				.amount(BigDecimal.TEN)
				.build();
		when(betRepository.findByEventIdAndStatus(10L, BetStatus.PENDING)).thenReturn(List.of(bet));

		betService.publishBetResults(10L, 9L);

		ArgumentCaptor<BetSettlementMessage> captor = forClass(BetSettlementMessage.class);
		verify(betSettlementProducer).sendBetSettlement(captor.capture());
		assertThat(captor.getValue().getBetId()).isEqualTo(2L);
		assertThat(captor.getValue().isWon()).isFalse();
	}

	@Test
	void publishBetResults_noPendingBets_doesNotSendAnyMessages() {
		when(betRepository.findByEventIdAndStatus(anyLong(), any())).thenReturn(List.of());

		betService.publishBetResults(10L, 5L);

		verifyNoInteractions(betSettlementProducer);
	}

	@Test
	void publishBetResults_multipleBets_sendsOneMessagePerBet() {
		Bet won = Bet.builder()
				.id(1L)
				.eventId(10L)
				.eventWinnerId(5L)
				.amount(BigDecimal.TEN)
				.build();
		Bet lost = Bet.builder()
				.id(2L)
				.eventId(10L)
				.eventWinnerId(3L)
				.amount(BigDecimal.ONE)
				.build();

		when(betRepository.findByEventIdAndStatus(10L, BetStatus.PENDING)).thenReturn(List.of(won, lost));

		betService.publishBetResults(10L, 5L);

		verify(betSettlementProducer, times(2)).sendBetSettlement(any());
	}
}
