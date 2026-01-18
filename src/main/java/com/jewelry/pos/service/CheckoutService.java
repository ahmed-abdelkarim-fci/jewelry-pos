package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.*;
import com.jewelry.pos.domain.repository.*;
import com.jewelry.pos.web.dto.OldGoldRequestDTO;
import com.jewelry.pos.web.dto.SaleRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final HardwareService hardwareService;
    private final SystemConfigService configService;
    private final OldGoldService oldGoldService; // <--- 1. Inject New Service

    // Self-Inject to allow internal method calls to go through the Spring Proxy
    @Autowired
    @Lazy
    private CheckoutService self;

    /**
     * Orchestrator Method:
     * 1. Persist Data (Critical, Transactional)
     * 2. Trigger Hardware (Non-Critical, Retryable)
     */
    public void processSale(SaleRequestDTO request) {
        // Step 1: Execute DB Transaction via the 'self' proxy
        Sale savedSale = self.executeFinancialTransaction(request);

        // Step 2: Trigger Hardware with Automatic Retries
        try {
            self.triggerHardwareSafely(savedSale.getId());
        } catch (Exception e) {
            log.error("CRITICAL HARDWARE FAILURE: Cash drawer failed to open for Sale {}. Please use manual key.", savedSale.getId(), e);
        }
    }

    /**
     * DATABASE BOUNDARY
     * This method is transactional. If it fails, no money is recorded.
     */
    @Transactional
    public Sale executeFinancialTransaction(SaleRequestDTO request) {
        // 1. Create the Parent Sale Object
        Sale sale = new Sale();

        // Validate Request Integrity
        Set<String> uniqueRequestBarcodes = new HashSet<>(request.barcodes());
        if (uniqueRequestBarcodes.size() < request.barcodes().size()) {
            throw new IllegalArgumentException("Duplicate items detected in the cart. Please check scanned list.");
        }

        sale.setCustomerName(request.customerName());
        sale.setCustomerPhone(request.customerPhone());

        BigDecimal totalCartAmount = BigDecimal.ZERO;

        // 2. Loop through each New Item (Selling)
        for (String barcode : uniqueRequestBarcodes) {
            Product product = productRepository.findByBarcode(barcode)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + barcode));

            // A. Inventory Check
            if (product.getStatus() == ProductStatus.SOLD) {
                throw new IllegalStateException("Item '" + product.getModelName() + "' (" + barcode + ") is ALREADY SOLD!");
            }

            // B. Calculate Item Financials
            BigDecimal itemGoldPrice = product.getGrossWeight().multiply(request.currentGoldRate());
            BigDecimal itemTotal = itemGoldPrice.add(product.getMakingCharge());

            // C. Create Line Item Snapshot
            SaleItem item = new SaleItem();
            item.setProduct(product);
            item.setAppliedGoldRate(request.currentGoldRate());
            item.setWeightSnapshot(product.getGrossWeight());
            item.setPriceSnapshot(itemTotal);

            // D. Link to Parent Sale
            sale.addItem(item);

            // E. Add to Total
            totalCartAmount = totalCartAmount.add(itemTotal);

            // F. Mark Product as SOLD
            product.setStatus(ProductStatus.SOLD);
            productRepository.save(product);
        }

        // 3. Set Gross Total (Before Trade-in)
        sale.setTotalAmount(totalCartAmount);

        // 4. Save Sale FIRST to generate the ID (Needed to link Old Gold records)
        Sale savedSale = saleRepository.save(sale);

        // 5. Process Trade-Ins (Old Gold)
        BigDecimal totalOldGoldValue = BigDecimal.ZERO;

        if (request.tradeInItems() != null && !request.tradeInItems().isEmpty()) {
            for (OldGoldRequestDTO oldItem : request.tradeInItems()) {
                // This saves the Old Gold record and updates the Scrap Inventory
                // We pass the savedSale.getId() to link them
                BigDecimal itemValue = oldGoldService.processOldGoldPurchase(oldItem, savedSale.getId());
                totalOldGoldValue = totalOldGoldValue.add(itemValue);
            }
        }

        // 6. Final Calculations (Net Pay)
        BigDecimal netToPay = totalCartAmount.subtract(totalOldGoldValue);

        savedSale.setOldGoldTotalValue(totalOldGoldValue);
        savedSale.setNetCashPaid(netToPay);

        // 7. Update and Final Save
        Sale finalSale = saleRepository.save(savedSale);

        log.info("Financial Transaction Persisted: Sale ID {} | Total: {} | Old Gold: {} | Net Paid: {}",
                finalSale.getId(), finalSale.getTotalAmount(), totalOldGoldValue, netToPay);

        return finalSale;
    }

    /**
     * HARDWARE BOUNDARY
     */
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void triggerHardwareSafely(String saleId) {
        if (!configService.isHardwareEnabled()) {
            log.info("Hardware disabled in settings. Skipping cash drawer/printer.");
            return;
        }

        log.info("Attempting hardware trigger for Sale {}", saleId);
        hardwareService.openCashDrawer();
    }
}