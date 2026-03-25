package com.betting.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class BetSettlementMessage implements Serializable {

    private String betId;
    private String userId;
    private String eventId;
    private BigDecimal amount;
    private boolean won;
    private String betWinnerId;
    private String eventWinnerId;

    @JsonCreator
    public BetSettlementMessage(
            @JsonProperty("betId") String betId,
            @JsonProperty("userId") String userId,
            @JsonProperty("eventId") String eventId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("won") boolean won,
            @JsonProperty("betWinnerId") String betWinnerId,
            @JsonProperty("eventWinnerId") String eventWinnerId) {
        this.betId = betId;
        this.userId = userId;
        this.eventId = eventId;
        this.amount = amount;
        this.won = won;
        this.betWinnerId = betWinnerId;
        this.eventWinnerId = eventWinnerId;
    }
}
