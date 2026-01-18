package com.jewelry.pos.web.controller;

import com.jewelry.pos.service.UserManagementService;
import com.jewelry.pos.web.dto.CreateUserDTO;
import com.jewelry.pos.web.dto.UpdateUserDTO;
import com.jewelry.pos.web.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import this
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "User & Role Management")
public class UserAdminController {

    private final UserManagementService userService;

    @PostMapping("/users")
    // CHECK: Does the user have P1 (USER_MANAGE)?
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody CreateUserDTO dto) {
        userService.createUser(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "Update user details (Roles, Status, Name)")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdDate") Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{identifier}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "Get user by ID or Username")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String identifier) {
        return ResponseEntity.ok(userService.getUserByIdOrUsername(identifier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Operation(summary = "Remove a user permanently")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Seed is usually open on startup or protected heavily
    @PostMapping("/seed")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> seedData() {
        userService.seedInitialData();
        return ResponseEntity.ok("Seeded.");
    }

    @PostMapping("/backup")
    @PreAuthorize("hasAuthority('USER_MANAGE')") // Only Super Admin
    @Operation(summary = "Trigger a manual database backup")
    public ResponseEntity<String> triggerBackup() {
        try {
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            java.nio.file.Path source = java.nio.file.Paths.get("data", "jewelry_db.mv.db");
            java.nio.file.Path backupDir = java.nio.file.Paths.get("backups");
            java.nio.file.Path target = backupDir.resolve("jewelry_db_backup_" + timestamp + ".mv.db");

            if (!java.nio.file.Files.exists(backupDir)) {
                java.nio.file.Files.createDirectories(backupDir);
            }

            java.nio.file.Files.copy(source, target);
            return ResponseEntity.ok("Backup created successfully: " + target.getFileName());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Backup failed: " + e.getMessage());
        }
    }
}