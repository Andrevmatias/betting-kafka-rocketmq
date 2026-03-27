package com.betting.api.model;

import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Getter
public class EventOutcomeRequest {
	@Positive(message = "eventId must be greater than zero")
	private Long eventId;
	@NotBlank(message = "eventName is required")
	private String eventName;
	@NotBlank(message = "eventWinnerId is required")
	private Long eventWinnerId;
}
