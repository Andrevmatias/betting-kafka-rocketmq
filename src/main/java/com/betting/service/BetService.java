package com.betting.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.betting.data.model.Bet;
import com.betting.data.repository.BetRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetService {

	private final BetRepository betRepository;

	public List<Bet> getAllBets() {
		return betRepository.findAll();
	}
}
