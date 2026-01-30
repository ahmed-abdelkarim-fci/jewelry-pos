package com.jewelry.pos.web.controller;

import com.jewelry.pos.service.ZReportService;
import com.jewelry.pos.web.dto.RecentTransactionDTO;
import com.jewelry.pos.web.dto.ZReportDTO;
import com.jewelry.pos.service.ReceiptService;
import com.jewelry.pos.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Financial Reporting")
public class ReportController {

    private final ZReportService zReportService;
    private final ReceiptService receiptService;
    private final LabelService labelService;

    @GetMapping("/transactions")
    @Operation(summary = "Get recent transactions for reports screen")
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<java.util.List<RecentTransactionDTO>> getRecentTransactions() {
        return ResponseEntity.ok(zReportService.getRecentTransactions());
    }

    @GetMapping("/z-report")
    @Operation(summary = "Get End of Day Report (Revenue & Weight)")
    // CHECK: P2 (PRODUCT_MANAGE) required.
    // This blocks regular cashiers (P3) from seeing total revenue.
    @PreAuthorize("hasAuthority('PRODUCT_MANAGE')")
    public ResponseEntity<ZReportDTO> getZReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // Default to today if no date provided
        LocalDate reportDate = (date != null) ? date : LocalDate.now();

        return ResponseEntity.ok(zReportService.generateEndOfDayReport(reportDate));
    }

    @GetMapping(value = "/receipt/{saleId}", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SALE_EXECUTE')")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String saleId) {
        byte[] pdfBytes = receiptService.generateReceiptPdf(saleId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=receipt_" + saleId + ".pdf")
                .body(pdfBytes);
    }

    // Inside InventoryController or ReportController
    @GetMapping("/label/{barcode}")
    public ResponseEntity<String> getLabelZpl(@PathVariable String barcode) {
        // Content-Type "text/plain" is fine for raw ZPL
        return ResponseEntity.ok(labelService.generateZplLabel(barcode));
    }
}