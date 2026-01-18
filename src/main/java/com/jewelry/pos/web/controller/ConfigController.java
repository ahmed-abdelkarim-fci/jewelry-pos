package com.jewelry.pos.web.controller;

import com.jewelry.pos.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "System Configuration")
public class ConfigController {

    private final SystemConfigService configService;

    @GetMapping("/gold-update")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')") // Only Managers
    @Operation(summary = "Get status of Gold Auto-Update")
    public ResponseEntity<Map<String, Boolean>> getGoldUpdateStatus() {
        boolean isEnabled = configService.isGoldAutoUpdateEnabled();
        return ResponseEntity.ok(Map.of("enabled", isEnabled));
    }

    @PutMapping("/gold-update")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Enable/Disable Gold Auto-Update")
    public ResponseEntity<Void> setGoldUpdateStatus(@RequestBody Map<String, Boolean> payload) {
        Boolean enabled = payload.get("enabled");
        if (enabled == null) throw new IllegalArgumentException("Field 'enabled' is required");
        
        configService.setGoldAutoUpdate(enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hardware")
    @PreAuthorize("hasAuthority('USER_MANAGE')") // Only Admins should change hardware settings
    @Operation(summary = "Get status of Hardware Integration")
    public ResponseEntity<Map<String, Boolean>> getHardwareStatus() {
        return ResponseEntity.ok(Map.of("enabled", configService.isHardwareEnabled()));
    }

    @PutMapping("/hardware")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "Enable/Disable Hardware (Printer/Drawer)")
    public ResponseEntity<Void> setHardwareStatus(@RequestBody Map<String, Boolean> payload) {
        Boolean enabled = payload.get("enabled");
        if (enabled == null) throw new IllegalArgumentException("Field 'enabled' is required");

        configService.setHardwareEnabled(enabled);
        return ResponseEntity.ok().build();
    }
}