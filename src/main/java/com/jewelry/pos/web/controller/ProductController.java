package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.service.InventoryService;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.ProductRequestDTO;
import com.jewelry.pos.web.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products - Frontend Compatible")
public class ProductController {

    private final InventoryService inventoryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @GetMapping
    @Operation(summary = "Get all products with pagination")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<Page<ProductLiteDTO>> getAllProducts(
            @PageableDefault(size = 20, sort = "modelName") Pageable pageable
    ) {
        return ResponseEntity.ok(inventoryService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<ProductLiteDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(inventoryService.getProductById(id));
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Get product by barcode (for POS scanning)")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<ProductLiteDTO> getProductByBarcode(@PathVariable String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(productMapper::toLiteDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        inventoryService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<Void> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequestDTO request) {
        inventoryService.updateProduct(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Delete a product")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by query")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<List<ProductLiteDTO>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(inventoryService.searchProducts(query));
    }
}
