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
public class EventOutcomeMessage implements Serializable {

	private Long eventId;
	private String eventName;
	private Long winnerId;

	@JsonCreator
	public EventOutcomeMessage(
			@JsonProperty("eventId") Long eventId,
			@JsonProperty("eventName") String eventName,
			@JsonProperty("winnerId") Long winnerId) {
		this.eventId = eventId;
		this.eventName = eventName;
		this.winnerId = winnerId;
	}
}
