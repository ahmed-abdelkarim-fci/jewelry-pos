package com.jewelry.pos.web.mapper;

import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.ProductRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected com.jewelry.pos.service.GoldRateService goldRateService;

    // Entity to Lite DTO (Existing)
    @Mapping(target = "estimatedPrice", expression = "java(calculatePrice(product))")
    @Mapping(source = "createdDate", target = "createdDate")
    public abstract ProductLiteDTO toLiteDTO(Product product);

    // DTO to Entity (New: For creating products)
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true) // Status defaults to AVAILABLE
    public abstract Product toEntity(ProductRequestDTO dto);

    // Update Entity from DTO (New: For updating products)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "barcode", ignore = true) // Usually we don't change barcode on update
    public abstract void updateEntityFromDto(ProductRequestDTO dto, @MappingTarget Product product);

    // Dynamic pricing calculation based on current gold rate, purity, weight, and making charge
    protected BigDecimal calculatePrice(Product product) {
        if (product.getGrossWeight() == null || product.getMakingCharge() == null) {
            return BigDecimal.ZERO;
        }
        
        // Get current gold rate for the product's purity
        BigDecimal goldRatePerGram = goldRateService.getCurrentSellRateForPurity(product.getPurityEnum());
        
        // Calculate: (gold rate per gram * weight) + making charge
        BigDecimal goldValue = goldRatePerGram.multiply(product.getGrossWeight());
        return goldValue.add(product.getMakingCharge());
    }
}