package com.jewelry.pos.web.dto;

import java.math.BigDecimal;

public record PersonalAccountSummaryDTO(
        String personId,
        String personName,
        BigDecimal netMoney,
        BigDecimal netWeight,
        String moneyStatus,
        String weightStatus,
        long transactionCount
) {}
