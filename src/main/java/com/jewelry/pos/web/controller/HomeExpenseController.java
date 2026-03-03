package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.HomeExpense;
import com.jewelry.pos.service.HomeExpenseService;
import com.jewelry.pos.web.dto.HomeExpenseRequestDTO;
import com.jewelry.pos.web.dto.HomeExpenseSummaryDTO;
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

@RestController
@RequestMapping("/api/home-expenses")
@RequiredArgsConstructor
@Tag(name = "Home Expenses Management")
public class HomeExpenseController {

    private final HomeExpenseService homeExpenseService;

    @PostMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Create a new home expense transaction")
    public ResponseEntity<HomeExpense> createTransaction(@Valid @RequestBody HomeExpenseRequestDTO dto) {
        return ResponseEntity.ok(homeExpenseService.createTransaction(dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all home expense transactions with pagination")
    public ResponseEntity<Page<HomeExpense>> getAllTransactions(
            @PageableDefault(size = 20, sort = "lastModifiedDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(homeExpenseService.getAllTransactions(pageable));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get summary of all home expenses")
    public ResponseEntity<HomeExpenseSummaryDTO> getSummary() {
        return ResponseEntity.ok(homeExpenseService.getSummary());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Delete a home expense transaction")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        homeExpenseService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }
}
