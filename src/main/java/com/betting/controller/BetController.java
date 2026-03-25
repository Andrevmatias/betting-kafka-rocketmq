package com.betting.controller;

import com.betting.dto.BetRequest;
import com.betting.model.Bet;
import com.betting.service.BetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/bets")
@RequiredArgsConstructor
@Slf4j
public class BetController {

    private final BetService betService;

    @PostMapping
    public ResponseEntity<Bet> placeBet(@Valid @RequestBody BetRequest request) {
        log.debug("POST /bets - placing bet for userId={}", request.getUserId());
        Bet bet = betService.placeBet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bet);
    }

    @GetMapping
    public ResponseEntity<List<Bet>> getAllBets() {
        log.debug("GET /bets - listing all bets");
        return ResponseEntity.ok(betService.getAllBets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bet> getBetById(@PathVariable UUID id) {
        log.debug("GET /bets/{} - getting bet by id", id);
        try {
            return ResponseEntity.ok(betService.getBetById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bet>> getBetsByUserId(@PathVariable String userId) {
        log.debug("GET /bets/user/{} - getting bets by userId", userId);
        return ResponseEntity.ok(betService.getBetsByUserId(userId));
    }
}
