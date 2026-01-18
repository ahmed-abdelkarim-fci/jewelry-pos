package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record SaleRequestDTO(
        @NotEmpty(message = "Cart cannot be empty")
        List<String> barcodes,

        @NotNull(message = "Gold Rate is required")
        @Positive
        BigDecimal currentGoldRate,

        @NotEmpty(message = "customer name cannot be empty")
        String customerName,

        @NotEmpty(message = "customer phone cannot be empty")
        String customerPhone,

        List<OldGoldRequestDTO> tradeInItems
) {}