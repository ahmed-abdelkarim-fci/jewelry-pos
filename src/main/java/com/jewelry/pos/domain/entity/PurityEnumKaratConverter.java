package com.jewelry.pos.domain.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PurityEnumKaratConverter implements AttributeConverter<PurityEnum, String> {

    @Override
    public String convertToDatabaseColumn(PurityEnum attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case K24 -> "KARAT_24";
            case K21 -> "KARAT_21";
            case K18 -> "KARAT_18";
        };
    }

    @Override
    public PurityEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "KARAT_24", "K24" -> PurityEnum.K24;
            case "KARAT_21", "K21" -> PurityEnum.K21;
            case "KARAT_18", "K18" -> PurityEnum.K18;
            default -> throw new IllegalArgumentException("Unknown purity value: " + dbData);
        };
    }
}
