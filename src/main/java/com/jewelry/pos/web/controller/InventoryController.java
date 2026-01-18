package com.jewelry.pos.web.controller;

import com.jewelry.pos.service.InventoryService;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.ProductRequestDTO;
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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    // CHECK: Does the user have P2 (PRODUCT_MANAGE)?
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<Void> addProduct(@Valid @RequestBody ProductRequestDTO request) {
        inventoryService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<Void> updateProduct(@PathVariable String id, @Valid @RequestBody ProductRequestDTO request) {
        inventoryService.updateProduct(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        inventoryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all products (Paged)")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')") // Cashiers need to see list too
    public ResponseEntity<Page<ProductLiteDTO>> getAllProducts(
            @PageableDefault(size = 20, sort = "modelName") Pageable pageable
    ) {
        return ResponseEntity.ok(inventoryService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details by ID")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<ProductLiteDTO> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(inventoryService.getProductById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Autocomplete Search for Cashier (Manual Mode)")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')") // Cashiers need this!
    public ResponseEntity<List<ProductLiteDTO>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(inventoryService.searchProducts(query));
    }
}