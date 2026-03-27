package com.betting.messaging.model;

import lombok.Getter;

@Getter
public final class MessageTopics {
	private MessageTopics() {
	}

	public static final String EVENT_OUTCOMES = "event-outcomes";
	public static final String BET_SETTLEMENTS = "bet-settlements";
}
