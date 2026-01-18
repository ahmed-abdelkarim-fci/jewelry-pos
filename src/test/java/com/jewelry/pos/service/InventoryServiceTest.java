package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.domain.entity.Purity;
import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.web.dto.ProductRequestDTO;
import com.jewelry.pos.web.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private ProductMapper productMapper;
    @InjectMocks private InventoryService inventoryService;

    @Test
    void createProduct_ShouldThrowError_IfBarcodeExists() {
        ProductRequestDTO dto = new ProductRequestDTO(
            "123", "Ring", Purity.K21, BigDecimal.TEN, BigDecimal.ONE
        );

        when(productRepository.findByBarcode("123")).thenReturn(Optional.of(new Product()));

        assertThrows(IllegalStateException.class, () -> inventoryService.createProduct(dto));
    }

    @Test
    void createProduct_ShouldSave_IfNew() {
        ProductRequestDTO dto = new ProductRequestDTO(
            "NEW-123", "Ring", Purity.K21, BigDecimal.TEN, BigDecimal.ONE
        );

        when(productRepository.findByBarcode("NEW-123")).thenReturn(Optional.empty());
        when(productMapper.toEntity(dto)).thenReturn(new Product());

        inventoryService.createProduct(dto);

        verify(productRepository).save(any(Product.class));
    }
}