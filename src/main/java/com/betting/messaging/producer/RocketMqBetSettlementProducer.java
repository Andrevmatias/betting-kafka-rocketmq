package com.betting.messaging.producer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import com.betting.exception.BrokerException;
import com.betting.messaging.model.BetSettlementMessage;
import com.betting.messaging.model.MessageTopics;

@Component
@RequiredArgsConstructor
@Slf4j
public class RocketMqBetSettlementProducer implements BetSettlementProducer {

	private final RocketMQTemplate rocketMQTemplate;

	@Override public void sendBetSettlement(BetSettlementMessage message) {
		log.info("Sending bet settlement to RocketMQ topic={}: betId={}, won={}", MessageTopics.BET_SETTLEMENTS, message.getBetId(), message.isWon());

		CompletableFuture<SendResult> future = new CompletableFuture<>();
		rocketMQTemplate.asyncSend(MessageTopics.BET_SETTLEMENTS, message, new SendCallback() {
			@Override public void onSuccess(SendResult sendResult) {
				future.complete(sendResult);
			}

			@Override public void onException(Throwable ex) {
				future.completeExceptionally(ex);
			}
		});

		try {
			future.get();
			log.info("Bet settlement message sent successfully for betId={}", message.getBetId());
		} catch (ExecutionException ex) {
			log.error("Failed to send bet settlement message for betId={}: {}", message.getBetId(), ex.getCause().getMessage(), ex.getCause());
			throw new BrokerException("Failed to send bet settlement message", ex.getCause());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BrokerException("Interrupted while sending bet settlement message", e);
		}
	}
}
