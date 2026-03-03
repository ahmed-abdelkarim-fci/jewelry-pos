package com.jewelry.pos.web.dto;

import java.math.BigDecimal;

public record SupplierAccountSummaryDTO(
        String supplierId,
        String supplierName,
        BigDecimal netFees,
        BigDecimal netWeight,
        String feesStatus,
        String weightStatus,
        long transactionCount
) {}
