package com.betting.service;

import com.betting.dto.BetRequest;
import com.betting.model.Bet;
import com.betting.model.BetStatus;
import com.betting.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BetService {

    private final BetRepository betRepository;

    @Transactional
    public Bet placeBet(BetRequest request) {
        log.debug("Placing bet for userId={}, eventId={}, amount={}", request.getUserId(), request.getEventId(), request.getAmount());
        Bet bet = Bet.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .eventMarketId(request.getEventMarketId())
                .eventWinnerId(request.getEventWinnerId())
                .amount(request.getAmount())
                .status(BetStatus.PENDING)
                .build();
        Bet saved = betRepository.save(bet);
        log.info("Bet placed successfully: id={}, userId={}, eventId={}", saved.getId(), saved.getUserId(), saved.getEventId());
        return saved;
    }

    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }

    public Bet getBetById(UUID id) {
        return betRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bet not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Bet> getBetsByUserId(String userId) {
        return betRepository.findByUserId(userId);
    }

    @Transactional
    public Bet updateBetStatus(UUID betId, BetStatus status) {
        Bet bet = getBetById(betId);
        log.debug("Updating bet id={} status from {} to {}", betId, bet.getStatus(), status);
        bet.setStatus(status);
        return betRepository.save(bet);
    }
}
