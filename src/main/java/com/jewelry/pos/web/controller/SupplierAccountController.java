package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.SupplierAccount;
import com.jewelry.pos.service.SupplierAccountService;
import com.jewelry.pos.web.dto.SupplierAccountRequestDTO;
import com.jewelry.pos.web.dto.SupplierAccountSummaryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-accounts")
@RequiredArgsConstructor
@Tag(name = "Supplier Accounts Management")
public class SupplierAccountController {

    private final SupplierAccountService supplierAccountService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Create a new supplier account transaction")
    public ResponseEntity<SupplierAccount> createTransaction(@Valid @RequestBody SupplierAccountRequestDTO dto) {
        return ResponseEntity.ok(supplierAccountService.createTransaction(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all supplier account transactions with pagination")
    public ResponseEntity<Page<SupplierAccount>> getAllTransactions(
            @PageableDefault(size = 20, sort = "lastModifiedDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(supplierAccountService.getAllTransactions(pageable));
    }

    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all transactions for a specific supplier")
    public ResponseEntity<Page<SupplierAccount>> getTransactionsBySupplier(
            @PathVariable String supplierId,
            @PageableDefault(size = 20, sort = "lastModifiedDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(supplierAccountService.getTransactionsBySupplier(supplierId, pageable));
    }

    @GetMapping("/summaries")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get summary of all suppliers with net balances")
    public ResponseEntity<List<SupplierAccountSummaryDTO>> getSupplierSummaries() {
        return ResponseEntity.ok(supplierAccountService.getSupplierSummaries());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Delete a supplier account transaction")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        supplierAccountService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
