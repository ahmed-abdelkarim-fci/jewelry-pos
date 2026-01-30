package com.jewelry.pos.web.controller;

import com.jewelry.pos.service.OldGoldService;
import com.jewelry.pos.web.dto.OldGoldRequestDTO;
import com.jewelry.pos.web.dto.PurificationRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/old-gold")
@RequiredArgsConstructor
@Tag(name = "Old Gold & Scrap Management")
public class OldGoldController {

    private final OldGoldService oldGoldService;

    @PostMapping("/buy")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Buy Old Gold for Cash (Cash Out)")
    public ResponseEntity<Void> buyOldGold(@RequestBody OldGoldRequestDTO request) {
        // Pass null for saleId because this is a direct cash transaction
        oldGoldService.processOldGoldPurchase(request, null);
        return ResponseEntity.ok().build();
    }

    // توريد دهب لشخص او مصنع
    @PostMapping("/purify")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Purify Scrap (Sell to Factory)")
    public ResponseEntity<Void> purifyScrap(@RequestBody PurificationRequestDTO request) {
        oldGoldService.processPurification(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/scrap-inventory")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get current scrap inventory by karat")
    public ResponseEntity<?> getScrapInventory() {
        return ResponseEntity.ok(oldGoldService.getScrapInventory());
    }
    
    @GetMapping("/purchases")
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    @Operation(summary = "Get all old gold purchases with pagination")
    public ResponseEntity<?> getAllPurchases(
            @org.springframework.data.web.PageableDefault(size = 20, sort = "transactionDate", direction = org.springframework.data.domain.Sort.Direction.DESC) 
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(oldGoldService.getAllPurchases(pageable));
    }
}