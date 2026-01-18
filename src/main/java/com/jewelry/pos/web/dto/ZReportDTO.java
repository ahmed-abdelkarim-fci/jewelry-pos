package com.jewelry.pos.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ZReportDTO(
    LocalDate reportDate,
    int totalTransactions,
    BigDecimal totalRevenue,
    BigDecimal totalGoldWeightSold
) {}