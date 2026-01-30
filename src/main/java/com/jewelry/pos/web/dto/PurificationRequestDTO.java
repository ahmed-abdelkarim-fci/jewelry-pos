package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PurificationRequestDTO(

        @NotNull(message = "{validation.purification.purity.required}")
        String purity,

        @NotNull(message = "{validation.purification.weight.required}")
        @Positive(message = "{validation.purification.weight.positive}")
        BigDecimal weightToSell,

        @NotNull(message = "{validation.purification.cash.required}")
        @Positive(message = "{validation.purification.cash.positive}")
        BigDecimal cashReceived,

        //شخص عادي ممكن اسم
        @jakarta.validation.constraints.NotBlank(message = "{validation.purification.factoryName.required}")
        String factoryName
) {}