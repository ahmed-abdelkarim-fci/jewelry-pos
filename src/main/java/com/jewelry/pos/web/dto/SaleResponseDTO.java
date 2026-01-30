package com.jewelry.pos.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponseDTO(
    String id,
    LocalDateTime date,
    BigDecimal totalAmount,
    BigDecimal oldGoldTotalValue,
    BigDecimal netCashPaid,
    String createdBy,
    String customerName,
    String customerPhone,
    List<SaleItemDTO> items
) {
    public record SaleItemDTO(
        String productName,
        BigDecimal weight,
        BigDecimal priceSnapshot
    ) {}
}