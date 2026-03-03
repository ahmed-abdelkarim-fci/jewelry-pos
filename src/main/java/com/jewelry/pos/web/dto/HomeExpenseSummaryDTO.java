package com.jewelry.pos.web.dto;

import java.math.BigDecimal;

public record HomeExpenseSummaryDTO(
        BigDecimal totalMoneyReceivable,
        BigDecimal totalMoneyPayable,
        BigDecimal totalWeightReceivable,
        BigDecimal totalWeightPayable,
        BigDecimal netMoney,
        BigDecimal netWeight,
        long transactionCount
) {}
