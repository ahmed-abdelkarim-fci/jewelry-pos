package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.JewelryTypeEnum;
import com.jewelry.pos.domain.entity.PurityEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Barcode is required")
        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "Barcode contains invalid characters")
        String barcode,

        @NotBlank(message = "Model name is required")
        @Size(max = 100, message = "Model name too long")
        String modelName,

        @NotNull(message = "Purity is required")
        PurityEnum purityEnum,

        @NotNull(message = "Jewelry type is required")
        JewelryTypeEnum type,

        @NotNull(message = "Weight is required")
        @Positive(message = "Weight must be positive")
        @Digits(integer = 5, fraction = 3, message = "Weight format invalid (max 3 decimals)")
        BigDecimal grossWeight,

        @NotNull(message = "Making charge is required")
        @PositiveOrZero(message = "Making charge cannot be negative")
        @Digits(integer = 6, fraction = 2, message = "Making charge format invalid (max 2 decimals)")
        BigDecimal makingCharge,

        String description,

        @NotNull(message = "Cost Price is required for profit calculation")
        @PositiveOrZero(message = "Cost Price cannot be negative")
        BigDecimal costPrice
) {
}