package com.jewelry.pos.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record DashboardStatsDTO(
        BigDecimal salesRevenue,
        BigDecimal cost,
        BigDecimal netProfit,
        Long salesCount,
        Long itemsInStock,
        BigDecimal oldGoldBoughtWeight,
        BigDecimal oldGoldExpense,
        BigDecimal purificationIncome,
        Map<String, BigDecimal> scrapInventory,
        LocalDateTime lastUpdated,
        LocalDateTime fromDate,
        LocalDateTime toDate
) {}
