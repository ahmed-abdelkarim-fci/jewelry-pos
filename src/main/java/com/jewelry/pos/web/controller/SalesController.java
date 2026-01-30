package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.service.CheckoutService;
import com.jewelry.pos.service.SalesManagementService;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.SaleRequestDTO;
import com.jewelry.pos.web.dto.SaleResponseDTO;
import com.jewelry.pos.web.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Tag(name = "Sales Operations - Frontend Compatible")
public class SalesController {

    private final CheckoutService checkoutService;
    private final SalesManagementService salesManagementService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Create a new sale (checkout)")
    public ResponseEntity<SaleResponseDTO> createSale(@Valid @RequestBody SaleRequestDTO request) {
        checkoutService.processSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Get all sales with pagination")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<Page<SaleResponseDTO>> getAllSales(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(salesManagementService.searchSales(query, fromDate, toDate, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale details by ID")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable String id) {
        return ResponseEntity.ok(salesManagementService.getSaleById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Void/Refund a sale")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<Void> voidSale(@PathVariable String id) {
        salesManagementService.voidSale(id);
        return ResponseEntity.noContent().build();
    }
}
