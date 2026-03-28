package com.betting.api.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betting.api.model.EventOutcomeRequest;
import com.betting.data.model.Event;
import com.betting.service.EventOutcomeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/eventOutcomes")
@RequiredArgsConstructor
public class EventOutcomeController {

	private final EventOutcomeService eventService;

	@PostMapping
	public ResponseEntity<Event> publishOutcome(@Valid @RequestBody EventOutcomeRequest event) {
		eventService.publishOutcome(event.getEventId(), event.getEventWinnerId());
		return ResponseEntity.accepted().build();
	}
}
