package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.GoldRate;
import com.jewelry.pos.service.GoldRateService;
import com.jewelry.pos.web.dto.GoldRateRequestDTO;
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
@RequestMapping("/api/rates")
@RequiredArgsConstructor
@Tag(name = "Gold Rate Settings")
public class GoldRateController {

    private final GoldRateService goldRateService;

    @PostMapping
    // CHECK: P2 is required to SET the rate
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<Void> setDailyRate(@Valid @RequestBody GoldRateRequestDTO request) {
        goldRateService.setDailyRate(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/latest")
    // CHECK: P3 is enough to VIEW the rate (Even the lowest cashier needs this)
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<GoldRate> getLatestRate() {
        return ResponseEntity.ok(goldRateService.getLatestRate());
    }

    @GetMapping("/history")
    @Operation(summary = "Get historical gold rates")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')") // Only Managers care about history
    public ResponseEntity<Page<GoldRate>> getRateHistory(
            @PageableDefault(size = 10, sort = "effectiveDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(goldRateService.getRateHistory(pageable));
    }
}