package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record GoldRateRequestDTO(
        @NotNull @Positive @Digits(integer = 5, fraction = 2) BigDecimal rate24k,
        @NotNull @Positive @Digits(integer = 5, fraction = 2) BigDecimal rate21k,
        @NotNull @Positive @Digits(integer = 5, fraction = 2) BigDecimal rate18k
) {}