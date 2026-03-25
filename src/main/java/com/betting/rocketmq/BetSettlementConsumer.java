package com.betting.rocketmq;

import com.betting.dto.BetSettlementMessage;
import com.betting.model.Bet;
import com.betting.model.BetStatus;
import com.betting.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RocketMQMessageListener(
        topic = "bet-settlements",
        consumerGroup = "bet-settlement-consumer-group"
)
@RequiredArgsConstructor
@Slf4j
public class BetSettlementConsumer implements RocketMQListener<BetSettlementMessage> {

    private final BetRepository betRepository;

    @Override
    @Transactional
    public void onMessage(BetSettlementMessage message) {
        log.info("Received bet settlement from RocketMQ: betId={}, userId={}, won={}", message.getBetId(), message.getUserId(), message.isWon());

        try {
            UUID betId = UUID.fromString(message.getBetId());
            Bet bet = betRepository.findById(betId)
                    .orElseThrow(() -> new IllegalArgumentException("Bet not found with id: " + message.getBetId()));

            if (bet.getStatus() == BetStatus.SETTLED) {
                log.warn("Bet id={} is already SETTLED, skipping", betId);
                return;
            }

            bet.setStatus(BetStatus.SETTLED);
            betRepository.save(bet);

            log.info("Bet id={} marked as SETTLED. userId={}, eventId={}, amount={}, won={}",
                    betId, message.getUserId(), message.getEventId(), message.getAmount(), message.isWon());

        } catch (Exception e) {
            log.error("Error settling bet id={}: {}", message.getBetId(), e.getMessage(), e);
            throw e;
        }
    }
}
