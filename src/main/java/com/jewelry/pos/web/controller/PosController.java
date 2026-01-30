package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.service.CheckoutService;
import com.jewelry.pos.service.SalesManagementService;
import com.jewelry.pos.web.dto.ProductLiteDTO;
import com.jewelry.pos.web.dto.SaleRequestDTO;
import com.jewelry.pos.web.dto.SaleResponseDTO;
import com.jewelry.pos.web.mapper.ProductMapper;
import com.jewelry.pos.domain.entity.ProductStatusEnum;
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
@RequestMapping("/api/pos")
@RequiredArgsConstructor
@Tag(name = "POS Operations")
public class PosController {

    private final CheckoutService checkoutService;
    private final SalesManagementService salesManagementService; 
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // --- EXISTING SCAN & CHECKOUT ---

    @PostMapping("/scan/{barcode}")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<ProductLiteDTO> scanItem(@PathVariable String barcode) {
        return productRepository.findByBarcode(barcode)
                .filter(p -> p.getStatus() == ProductStatusEnum.AVAILABLE)
                .map(productMapper::toLiteDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<Void> checkout(@Valid @RequestBody SaleRequestDTO request) {
        checkoutService.processSale(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // --- NEW HISTORY & VOID OPERATIONS ---

    @GetMapping("/sales")
    @Operation(summary = "Search Sales History (ID, Name, Date)")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<Page<SaleResponseDTO>> searchSales(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(salesManagementService.searchSales(query, fromDate, toDate, pageable));
    }

    @GetMapping("/sales/{id}")
    @Operation(summary = "View Single Receipt")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<SaleResponseDTO> getSaleDetails(@PathVariable String id) {
        return ResponseEntity.ok(salesManagementService.getSaleById(id));
    }

    @DeleteMapping("/sales/{id}")
    @Operation(summary = "Void/Refund a Sale")
    // CHECK: Only Managers (P2) can delete money!
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<Void> voidSale(@PathVariable String id) {
        salesManagementService.voidSale(id);
        return ResponseEntity.noContent().build();
    }
}