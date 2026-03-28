package com.betting.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betting.data.model.Bet;
import com.betting.service.BetService;

@RestController
@RequestMapping("/api/v1/bet")
@RequiredArgsConstructor
public class BetController {

	private final BetService betService;

	@GetMapping
	public ResponseEntity<List<Bet>> getAllBets() {
		return ResponseEntity.ok(betService.getAllBets());
	}
}
