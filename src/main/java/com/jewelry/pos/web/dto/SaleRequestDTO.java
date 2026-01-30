package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record SaleRequestDTO(
        @NotEmpty(message = "{validation.sale.cart.required}")
        List<String> barcodes,

        @NotNull(message = "{validation.sale.goldRate.required}")
        @Positive
        BigDecimal currentGoldRate,

        @NotEmpty(message = "{validation.sale.customerName.required}")
        String customerName,

        String customerPhone,

        List<OldGoldRequestDTO> tradeInItems
) {}