package com.betting.api.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betting.api.model.BetResponse;
import com.betting.mapper.BetMapper;
import com.betting.service.BetService;

@RestController
@RequestMapping("/api/v1/bet")
@RequiredArgsConstructor
public class BetController {

	private final BetService betService;
	private final BetMapper betMapper;

	@GetMapping
	public ResponseEntity<List<BetResponse>> getAllBets() {
		return ResponseEntity.ok(betService.getAllBets().stream()
				.map(betMapper::toResponse)
				.toList());
	}
}
