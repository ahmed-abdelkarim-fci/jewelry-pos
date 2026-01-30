package com.jewelry.pos.web.controller;

import com.jewelry.pos.service.DashboardService;
import com.jewelry.pos.web.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Manager Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')") // Managers Only
    @Operation(summary = "Get today's dashboard statistics")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(dashboardService.getTodayStats());
    }

    @GetMapping("/today")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')") // Managers Only
    @Operation(summary = "Get today's dashboard statistics (alias)")
    public ResponseEntity<Map<String, Object>> getTodayDashboard() {
        return ResponseEntity.ok(dashboardService.getTodayStats());
    }

    // ==========================================
    // ENHANCED DASHBOARD ENDPOINTS
    // ==========================================

    @GetMapping("/stats/range")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Get dashboard statistics for a custom date range")
    public ResponseEntity<DashboardStatsDTO> getStatsForDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(dashboardService.getStatsForDateRange(fromDate, toDate));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Get top selling products for a date range")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        LocalDate from = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate to = toDate != null ? toDate : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getTopProducts(from, to, limit));
    }

    @GetMapping("/user-performance")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Get user/cashier performance metrics for a date range")
    public ResponseEntity<List<UserPerformanceDTO>> getUserPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        LocalDate from = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate to = toDate != null ? toDate : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getUserPerformance(from, to));
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    @Operation(summary = "Get daily sales trends for a date range")
    public ResponseEntity<List<SalesTrendDTO>> getSalesTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        LocalDate from = fromDate != null ? fromDate : LocalDate.now().minusDays(7);
        LocalDate to = toDate != null ? toDate : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getSalesTrend(from, to));
    }
}