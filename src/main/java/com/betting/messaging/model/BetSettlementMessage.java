package com.betting.messaging.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class BetSettlementMessage implements Serializable {

	private Long betId;
	private Long userId;
	private Long eventId;
	private BigDecimal amount;
	private boolean won;
	private Long betWinnerId;
	private Long eventWinnerId;

	@JsonCreator
	public BetSettlementMessage(
			@JsonProperty("betId") Long betId,
			@JsonProperty("userId") Long userId,
			@JsonProperty("eventId") Long eventId,
			@JsonProperty("amount") BigDecimal amount,
			@JsonProperty("won") boolean won,
			@JsonProperty("betWinnerId") Long betWinnerId,
			@JsonProperty("eventWinnerId") Long eventWinnerId) {
		this.betId = betId;
		this.userId = userId;
		this.eventId = eventId;
		this.amount = amount;
		this.won = won;
		this.betWinnerId = betWinnerId;
		this.eventWinnerId = eventWinnerId;
	}
}
