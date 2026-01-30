package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.PurityEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record OldGoldRequestDTO(

        @NotBlank(message = "Karat type is required (e.g., KARAT_21)")
        PurityEnum purity,

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be greater than zero")
        BigDecimal weight,

        @NotNull(message = "Buy Rate is required")
        @Positive(message = "Buy Rate must be greater than zero")
        BigDecimal buyRate, // Price per gram

        String description, // Optional

        @NotBlank(message = "Customer National ID is required for security regulations")
        String customerNationalId,

        String customerPhoneNumber
) {}