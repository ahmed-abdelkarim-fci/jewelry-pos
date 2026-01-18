package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.*;
import com.jewelry.pos.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    // Sales & Inventory Repositories
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    // Old Gold & Scrap Repositories
    private final OldGoldPurchaseRepository oldGoldRepository;
    private final ScrapInventoryRepository scrapInventoryRepository;
    private final ScrapPurificationRepository purificationRepository;

    public Map<String, Object> getTodayStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // ==========================================
        // 1. SALES & PROFIT ANALYTICS
        // ==========================================
        List<Sale> sales = saleRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);
        long availableItems = productRepository.countByStatus(ProductStatus.AVAILABLE);

        BigDecimal totalRevenue = BigDecimal.ZERO; // Total Selling Price
        BigDecimal totalCost = BigDecimal.ZERO;    // Total Cost Price (Acquisition)

        for (Sale sale : sales) {
            totalRevenue = totalRevenue.add(sale.getTotalAmount());

            for (SaleItem item : sale.getItems()) {
                // Calculate Cost of Goods Sold (COGS)
                BigDecimal itemCost = item.getProduct().getCostPrice();
                if (itemCost != null) {
                    totalCost = totalCost.add(itemCost);
                }
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalCost);

        // ==========================================
        // 2. OLD GOLD ANALYTICS (Buying/Trade-In)
        // ==========================================
        List<OldGoldPurchase> oldGoldPurchases = oldGoldRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);

        BigDecimal oldGoldBoughtWeight = BigDecimal.ZERO;
        BigDecimal oldGoldExpense = BigDecimal.ZERO; // Cash Out (or Trade-in Value)

        for (OldGoldPurchase purchase : oldGoldPurchases) {
            oldGoldBoughtWeight = oldGoldBoughtWeight.add(purchase.getWeight());
            oldGoldExpense = oldGoldExpense.add(purchase.getTotalValue());
        }

        // ==========================================
        // 3. PURIFICATION ANALYTICS (Factory)
        // ==========================================
        List<ScrapPurification> purifications = purificationRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);

        BigDecimal purificationIncome = purifications.stream()
                .map(ScrapPurification::getCashReceived)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ==========================================
        // 4. CURRENT SCRAP INVENTORY (Live Status)
        // ==========================================
        List<ScrapInventory> scrapList = scrapInventoryRepository.findAll();

        // Convert List to Map for easier UI display: { "KARAT_21": 150.500, "KARAT_18": 40.000 }
        Map<String, BigDecimal> scrapBoxMap = scrapList.stream()
                .collect(Collectors.toMap(ScrapInventory::getKarat, ScrapInventory::getTotalWeight));

        // ==========================================
        // 5. BUILD RESPONSE
        // ==========================================
        return Map.of(
                // Sales Section
                "salesRevenue", totalRevenue,
                "cost", totalCost,
                "netProfit", netProfit,
                "salesCount", (long) sales.size(),
                "itemsInStock", availableItems,

                // Old Gold Section (Today's Activity)
                "oldGoldBoughtWeight", oldGoldBoughtWeight,
                "oldGoldExpense", oldGoldExpense,

                // Factory Section (Today's Activity)
                "purificationIncome", purificationIncome,

                // Inventory Section (Current State)
                "scrapInventory", scrapBoxMap,

                "lastUpdated", LocalDateTime.now()
        );
    }
}