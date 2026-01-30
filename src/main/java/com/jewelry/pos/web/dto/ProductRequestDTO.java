package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.JewelryTypeEnum;
import com.jewelry.pos.domain.entity.PurityEnum;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequestDTO(
//        @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "{validation.product.barcode.invalid}")
        String barcode,

        @NotBlank(message = "{validation.product.modelName.required}")
        @Size(max = 100, message = "{validation.product.modelName.size}")
        String modelName,

        @NotNull(message = "{validation.product.purity.required}")
        PurityEnum purityEnum,

        @NotNull(message = "{validation.product.type.required}")
        JewelryTypeEnum type,

        @NotNull(message = "{validation.product.weight.required}")
        @Positive(message = "{validation.product.weight.positive}")
        @Digits(integer = 5, fraction = 3, message = "{validation.product.weight.digits}")
        BigDecimal grossWeight,

        @NotNull(message = "{validation.product.makingCharge.required}")
        @PositiveOrZero(message = "{validation.product.makingCharge.nonNegative}")
        @Digits(integer = 6, fraction = 2, message = "{validation.product.makingCharge.digits}")
        BigDecimal makingCharge,

        String description,

        @NotNull(message = "{validation.product.costPrice.required}")
        @PositiveOrZero(message = "{validation.product.costPrice.nonNegative}")
        BigDecimal costPrice
) {
}