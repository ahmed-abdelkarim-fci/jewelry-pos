package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.ProductStatus;
import com.jewelry.pos.domain.entity.Sale;
import com.jewelry.pos.domain.entity.SaleItem;
import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.domain.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public Map<String, Object> getTodayStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 1. Fetch Sales List (Required to calculate Profit loop)
        List<Sale> sales = saleRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);
        long availableItems = productRepository.countByStatus(ProductStatus.AVAILABLE);

        // 2. Calculate Revenue, Cost, and Profit
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Sale sale : sales) {
            // Add Revenue (What Customer Paid)
            totalRevenue = totalRevenue.add(sale.getTotalAmount());

            // Add Cost (What Shop Paid)
            for (SaleItem item : sale.getItems()) {
                // IMPORTANT: Ensure Product entity has 'costPrice' field populated
                BigDecimal itemCost = item.getProduct().getCostPrice();
                if (itemCost != null) {
                    totalCost = totalCost.add(itemCost);
                }
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalCost);

        // 3. Return Data Map
        return Map.of(
                "revenue", totalRevenue,
                "cost", totalCost,
                "netProfit", netProfit,  // <--- The Critical Business Metric
                "salesCount", (long) sales.size(),
                "itemsInStock", availableItems,
                "lastUpdated", LocalDateTime.now()
        );
    }
}