package com.jewelry.pos.web.dto;
import java.math.BigDecimal;
public record ProductLiteDTO(String name, BigDecimal grossWeight, BigDecimal estimatedPrice) {}