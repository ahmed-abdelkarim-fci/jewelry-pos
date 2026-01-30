package com.jewelry.pos.web.dto;

import java.math.BigDecimal;

public record TopProductDTO(
        String productId,
        String barcode,
        String modelName,
        Long salesCount,
        BigDecimal totalRevenue,
        BigDecimal totalWeight
) {}
