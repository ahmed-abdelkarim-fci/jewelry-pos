package com.jewelry.pos.web.dto;

import java.math.BigDecimal;

public record UserPerformanceDTO(
        String userId,
        String username,
        String fullName,
        Long salesCount,
        BigDecimal totalRevenue,
        BigDecimal averageSaleValue
) {}
