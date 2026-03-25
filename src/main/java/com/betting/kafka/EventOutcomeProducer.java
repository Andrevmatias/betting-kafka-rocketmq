package com.betting.kafka;

import com.betting.dto.EventOutcomeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventOutcomeProducer {

    private static final String TOPIC = "event-outcomes";

    private final KafkaTemplate<String, EventOutcomeMessage> kafkaTemplate;

    public void sendEventOutcome(EventOutcomeMessage message) {
        log.info("Sending event outcome to Kafka topic={}: eventId={}, winnerId={}", TOPIC, message.getEventId(), message.getWinnerId());
        CompletableFuture<SendResult<String, EventOutcomeMessage>> future =
                kafkaTemplate.send(TOPIC, message.getEventId(), message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send event outcome message for eventId={}: {}", message.getEventId(), ex.getMessage(), ex);
            } else {
                log.info("Event outcome message sent successfully for eventId={}, offset={}",
                        message.getEventId(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
