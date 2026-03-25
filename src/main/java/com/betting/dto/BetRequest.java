package com.betting.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "eventId is required")
    private String eventId;

    @NotBlank(message = "eventMarketId is required")
    private String eventMarketId;

    @NotBlank(message = "eventWinnerId is required")
    private String eventWinnerId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private BigDecimal amount;
}
