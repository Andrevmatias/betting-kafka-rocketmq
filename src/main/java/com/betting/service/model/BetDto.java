package com.betting.service.model;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

import com.betting.data.model.BetStatus;

@Getter
@Builder
public class BetDto {

	private Long id;
	private Long userId;
	private Long eventId;
	private Long eventMarketId;
	private Long eventWinnerId;
	private BigDecimal amount;
	private BetStatus status;
}
