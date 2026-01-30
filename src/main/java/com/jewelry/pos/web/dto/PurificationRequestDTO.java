package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PurificationRequestDTO(

        @NotNull(message = "Karat is required (e.g., KARAT_21)")
        String purity,

        @NotNull(message = "Weight to sell is required")
        @Positive(message = "Weight must be greater than zero")
        BigDecimal weightToSell,

        @NotNull(message = "Cash received is required")
        @Positive(message = "Cash received cannot be negative")
        BigDecimal cashReceived,

        //شخص عادي ممكن اسم
        @jakarta.validation.constraints.NotBlank(message = "Factory Name is required for auditing")
        String factoryName
) {}