package com.jewelry.pos.web.mapper;

import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void calculatePrice_ShouldUseFixedRate() {
        Product p = new Product();
        p.setGrossWeight(new BigDecimal("2.000")); // 2 grams
        
        // Logic in mapper is Weight * 250.00
        // 2 * 250 = 500
        BigDecimal expected = new BigDecimal("500.000"); // Scale might vary based on impl
        
        ProductLiteDTO dto = mapper.toLiteDTO(p);
        
        // Use compareTo for BigDecimal safety
        assertEquals(0, expected.compareTo(dto.estimatedPrice()));
    }
}