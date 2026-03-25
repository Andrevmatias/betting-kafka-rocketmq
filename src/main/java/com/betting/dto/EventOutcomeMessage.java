package com.betting.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class EventOutcomeMessage implements Serializable {

    private String eventId;
    private String eventName;
    private String winnerId;

    @JsonCreator
    public EventOutcomeMessage(
            @JsonProperty("eventId") String eventId,
            @JsonProperty("eventName") String eventName,
            @JsonProperty("winnerId") String winnerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.winnerId = winnerId;
    }
}
