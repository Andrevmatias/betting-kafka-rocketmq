package com.betting.kafka;

import com.betting.dto.BetSettlementMessage;
import com.betting.dto.EventOutcomeMessage;
import com.betting.model.Bet;
import com.betting.model.BetStatus;
import com.betting.repository.BetRepository;
import com.betting.rocketmq.BetSettlementProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeConsumer {

    private final BetRepository betRepository;
    private final BetSettlementProducer betSettlementProducer;

    @KafkaListener(topics = "event-outcomes", groupId = "betting-group")
    public void handleEventOutcome(EventOutcomeMessage message) {
        log.info("Received event outcome from Kafka: eventId={}, winnerId={}", message.getEventId(), message.getWinnerId());

        List<Bet> bets = betRepository.findByEventId(message.getEventId());
        log.debug("Found {} bets for eventId={}", bets.size(), message.getEventId());

        for (Bet bet : bets) {
            if (bet.getStatus() != BetStatus.PENDING) {
                log.debug("Skipping bet id={} with non-PENDING status={}", bet.getId(), bet.getStatus());
                continue;
            }

            boolean won = bet.getEventWinnerId().equals(message.getWinnerId());
            BetStatus newStatus = won ? BetStatus.WON : BetStatus.LOST;

            bet.setStatus(newStatus);
            betRepository.save(bet);

            BetSettlementMessage settlementMessage = BetSettlementMessage.builder()
                    .betId(bet.getId().toString())
                    .userId(bet.getUserId())
                    .eventId(bet.getEventId())
                    .amount(bet.getAmount())
                    .won(won)
                    .betWinnerId(bet.getEventWinnerId())
                    .eventWinnerId(message.getWinnerId())
                    .build();

            log.info("Sending bet settlement to RocketMQ: betId={}, userId={}, won={}", bet.getId(), bet.getUserId(), won);
            betSettlementProducer.sendBetSettlement(settlementMessage);
        }
    }
}
