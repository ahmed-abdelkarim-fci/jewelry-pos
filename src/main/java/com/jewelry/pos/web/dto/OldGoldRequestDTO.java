package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record OldGoldRequestDTO(
    @NotNull String karat, // KARAT_21
    @Positive BigDecimal weight,
    @Positive BigDecimal buyRate, // Price per gram
    String description,
    String customerNationalId
) {}