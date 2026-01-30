package com.jewelry.pos.web.mapper;

import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.ProductRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Entity to Lite DTO (Existing)
    @Mapping(target = "estimatedPrice", expression = "java(calculatePrice(product))")
    @Mapping(source = "createdDate", target = "createdDate")
    ProductLiteDTO toLiteDTO(Product product);

    // DTO to Entity (New: For creating products)
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true) // Status defaults to AVAILABLE
    Product toEntity(ProductRequestDTO dto);

    // Update Entity from DTO (New: For updating products)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "barcode", ignore = true) // Usually we don't change barcode on update
    void updateEntityFromDto(ProductRequestDTO dto, @MappingTarget Product product);

    //TODO we need to calculate price based on the purity price * the gross weight +  makingCharge
    default BigDecimal calculatePrice(Product product) {
        return product.getGrossWeight().multiply(new BigDecimal("250.00")); 
    }
}