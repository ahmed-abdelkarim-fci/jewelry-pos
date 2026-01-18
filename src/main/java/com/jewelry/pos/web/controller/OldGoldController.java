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

    @PostMapping("/purify")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Purify Scrap (Sell to Factory)")
    public ResponseEntity<Void> purifyScrap(@RequestBody PurificationRequestDTO request) {
        oldGoldService.processPurification(request);
        return ResponseEntity.ok().build();
    }
}