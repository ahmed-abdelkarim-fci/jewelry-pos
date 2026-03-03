package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.Supplier;
import com.jewelry.pos.service.SupplierService;
import com.jewelry.pos.web.dto.SupplierDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management")
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Create a new supplier")
    public ResponseEntity<Supplier> createSupplier(@Valid @RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.createSupplier(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Update supplier")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable String id, @Valid @RequestBody SupplierDTO dto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable String id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Delete supplier")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok().build();
    }
}
