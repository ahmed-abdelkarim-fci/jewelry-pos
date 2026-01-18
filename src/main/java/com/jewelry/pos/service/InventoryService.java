package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Product;
import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.ProductRequestDTO;
import com.jewelry.pos.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public Product createProduct(ProductRequestDTO dto) {
        if (productRepository.findByBarcode(dto.barcode()).isPresent()) {
            throw new IllegalStateException("Product with this barcode already exists.");
        }
        Product product = productMapper.toEntity(dto);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(String id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
        
        productMapper.updateEntityFromDto(dto, product);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String id) {
        // Hard Delete (or you could do Soft Delete with a boolean flag)
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product not found");
        }
        productRepository.deleteById(id);
    }

    // 1. Get All Products (Paged)
    public Page<ProductLiteDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toLiteDTO);
    }

    // 2. Get Single Product
    public ProductLiteDTO getProductById(String id) {
        return productRepository.findById(id)
                .map(productMapper::toLiteDTO)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
    }

    public List<ProductLiteDTO> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            return List.of(); // Return empty if nothing typed
        }

        return productRepository.searchAvailableProducts(query).stream()
                .map(productMapper::toLiteDTO)
                .collect(Collectors.toList());
    }


}