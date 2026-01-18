package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PurificationRequestDTO(
    @NotNull String karat,
    @Positive BigDecimal weightToSell,
    @Positive BigDecimal cashReceived,
    String factoryName
) {}