package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.JewelryTypeEnum;
import com.jewelry.pos.domain.entity.ProductStatusEnum;
import com.jewelry.pos.domain.entity.PurityEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductLiteDTO(
        String id,
        String barcode,
        String modelName,
        PurityEnum purityEnum,
        JewelryTypeEnum type,
        BigDecimal grossWeight,
        BigDecimal makingCharge,
        String description,
        ProductStatusEnum status,
        BigDecimal costPrice,
        BigDecimal estimatedPrice,
        LocalDateTime createdDate
) {}