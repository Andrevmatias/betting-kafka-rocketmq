package com.betting.rocketmq;

import com.betting.dto.BetSettlementMessage;
import com.betting.exception.BrokerException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BetSettlementProducer {

    private static final String TOPIC = "bet-settlements";

    private final RocketMQTemplate rocketMQTemplate;

    public void sendBetSettlement(BetSettlementMessage message) {
        log.info("Sending bet settlement to RocketMQ topic={}: betId={}, won={}", TOPIC, message.getBetId(), message.isWon());
        try {
            rocketMQTemplate.convertAndSend(TOPIC, message);
            log.info("Bet settlement message sent successfully for betId={}", message.getBetId());
        } catch (Exception e) {
            log.error("Failed to send bet settlement message for betId={}: {}", message.getBetId(), e.getMessage(), e);
            throw new BrokerException("Failed to send bet settlement message", e);
        }
    }
}
