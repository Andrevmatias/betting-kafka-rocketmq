package com.betting.messaging.model;

import java.io.Serializable;

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
	private boolean won;

	@JsonCreator
	public BetSettlementMessage(
			@JsonProperty("betId") Long betId,
			@JsonProperty("won") boolean won) {
		this.betId = betId;
		this.won = won;
	}
}
