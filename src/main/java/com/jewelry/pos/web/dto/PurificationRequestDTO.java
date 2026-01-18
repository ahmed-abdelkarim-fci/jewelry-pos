package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PurificationRequestDTO(

        @NotBlank(message = "Karat is required (e.g., KARAT_21)")
        String karat,

        @NotNull(message = "Weight to sell is required")
        @Positive(message = "Weight must be greater than zero")
        BigDecimal weightToSell,

        @NotNull(message = "Cash received is required")
        @Positive(message = "Cash received cannot be negative")
        BigDecimal cashReceived,

        @NotBlank(message = "Factory Name is required for auditing")
        String factoryName
) {}