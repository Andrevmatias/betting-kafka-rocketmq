package com.betting.api.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
public class EventOutcomeRequest {
	@Positive(message = "eventId must be greater than zero")
	private Long eventId;
	@NotBlank(message = "eventName is required")
	private String eventName;
	@Positive(message = "eventWinnerId must be greater than zero")
	private Long eventWinnerId;
}
