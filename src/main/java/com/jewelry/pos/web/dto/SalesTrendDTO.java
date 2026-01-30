package com.jewelry.pos.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalesTrendDTO(
        LocalDate date,
        Long salesCount,
        BigDecimal totalRevenue,
        BigDecimal netProfit
) {}
