package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.*;
import com.jewelry.pos.domain.repository.*;
import com.jewelry.pos.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    // Account Repositories
    private final PersonalAccountRepository personalAccountRepository;
    private final SupplierAccountRepository supplierAccountRepository;
    private final HomeExpenseRepository homeExpenseRepository;

    // User Repository for performance analytics
    private final UserRepository userRepository;

    public Map<String, Object> getTodayStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // ==========================================
        // 1. SALES & PROFIT ANALYTICS
        // ==========================================
        List<Sale> sales = saleRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);
        long availableItems = productRepository.countByStatus(ProductStatusEnum.AVAILABLE);

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
                .collect(Collectors.toMap(s -> s.getPurity().name(), ScrapInventory::getTotalWeight));

        // ==========================================
        // 5. PERSONAL ACCOUNTS ANALYTICS
        // ==========================================
        List<PersonalAccount> personalAccounts = personalAccountRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);
        
        BigDecimal personalMoneyReceivable = BigDecimal.ZERO;
        BigDecimal personalMoneyPayable = BigDecimal.ZERO;
        BigDecimal personalWeightReceivable = BigDecimal.ZERO;
        BigDecimal personalWeightPayable = BigDecimal.ZERO;
        
        for (PersonalAccount pa : personalAccounts) {
            if (pa.getTransactionType() == TransactionTypeEnum.RECEIVABLE) {
                personalMoneyReceivable = personalMoneyReceivable.add(pa.getMoney());
                personalWeightReceivable = personalWeightReceivable.add(pa.getWeight());
            } else {
                personalMoneyPayable = personalMoneyPayable.add(pa.getMoney());
                personalWeightPayable = personalWeightPayable.add(pa.getWeight());
            }
        }

        // ==========================================
        // 6. SUPPLIER ACCOUNTS ANALYTICS
        // ==========================================
        List<SupplierAccount> supplierAccounts = supplierAccountRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);
        
        BigDecimal supplierFeesReceivable = BigDecimal.ZERO;
        BigDecimal supplierFeesPayable = BigDecimal.ZERO;
        BigDecimal supplierWeightReceivable = BigDecimal.ZERO;
        BigDecimal supplierWeightPayable = BigDecimal.ZERO;
        
        for (SupplierAccount sa : supplierAccounts) {
            if (sa.getTransactionType() == TransactionTypeEnum.RECEIVABLE) {
                supplierFeesReceivable = supplierFeesReceivable.add(sa.getFees());
                supplierWeightReceivable = supplierWeightReceivable.add(sa.getWeight());
            } else {
                supplierFeesPayable = supplierFeesPayable.add(sa.getFees());
                supplierWeightPayable = supplierWeightPayable.add(sa.getWeight());
            }
        }

        // ==========================================
        // 7. HOME EXPENSES ANALYTICS
        // ==========================================
        List<HomeExpense> homeExpenses = homeExpenseRepository.findAllByTransactionDateBetween(startOfDay, endOfDay);
        
        BigDecimal homeMoneyReceivable = BigDecimal.ZERO;
        BigDecimal homeMoneyPayable = BigDecimal.ZERO;
        BigDecimal homeWeightReceivable = BigDecimal.ZERO;
        BigDecimal homeWeightPayable = BigDecimal.ZERO;
        
        for (HomeExpense he : homeExpenses) {
            if (he.getTransactionType() == TransactionTypeEnum.RECEIVABLE) {
                homeMoneyReceivable = homeMoneyReceivable.add(he.getMoney());
                homeWeightReceivable = homeWeightReceivable.add(he.getWeight());
            } else {
                homeMoneyPayable = homeMoneyPayable.add(he.getMoney());
                homeWeightPayable = homeWeightPayable.add(he.getWeight());
            }
        }

        // ==========================================
        // 8. BUILD RESPONSE
        // ==========================================
        Map<String, Object> response = new LinkedHashMap<>();
        
        // Sales Section
        response.put("salesRevenue", totalRevenue);
        response.put("cost", totalCost);
        response.put("netProfit", netProfit);
        response.put("salesCount", (long) sales.size());
        response.put("itemsInStock", availableItems);

        // Old Gold Section (Today's Activity)
        response.put("oldGoldBoughtWeight", oldGoldBoughtWeight);
        response.put("oldGoldExpense", oldGoldExpense);

        // Factory Section (Today's Activity)
        response.put("purificationIncome", purificationIncome);

        // Inventory Section (Current State)
        response.put("scrapInventory", scrapBoxMap);

        // Personal Accounts Section
        response.put("personalMoneyReceivable", personalMoneyReceivable);
        response.put("personalMoneyPayable", personalMoneyPayable);
        response.put("personalWeightReceivable", personalWeightReceivable);
        response.put("personalWeightPayable", personalWeightPayable);
        response.put("personalNetMoney", personalMoneyReceivable.subtract(personalMoneyPayable));
        response.put("personalNetWeight", personalWeightReceivable.subtract(personalWeightPayable));

        // Supplier Accounts Section
        response.put("supplierFeesReceivable", supplierFeesReceivable);
        response.put("supplierFeesPayable", supplierFeesPayable);
        response.put("supplierWeightReceivable", supplierWeightReceivable);
        response.put("supplierWeightPayable", supplierWeightPayable);
        response.put("supplierNetFees", supplierFeesReceivable.subtract(supplierFeesPayable));
        response.put("supplierNetWeight", supplierWeightReceivable.subtract(supplierWeightPayable));

        // Home Expenses Section
        response.put("homeMoneyReceivable", homeMoneyReceivable);
        response.put("homeMoneyPayable", homeMoneyPayable);
        response.put("homeWeightReceivable", homeWeightReceivable);
        response.put("homeWeightPayable", homeWeightPayable);
        response.put("homeNetMoney", homeMoneyReceivable.subtract(homeMoneyPayable));
        response.put("homeNetWeight", homeWeightReceivable.subtract(homeWeightPayable));

        response.put("lastUpdated", LocalDateTime.now());
        
        return response;
    }

    // ==========================================
    // ENHANCED DASHBOARD METHODS
    // ==========================================

    /**
     * Get dashboard stats for a custom date range
     */
    public DashboardStatsDTO getStatsForDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startOfPeriod = fromDate.atStartOfDay();
        LocalDateTime endOfPeriod = toDate.atTime(LocalTime.MAX);

        // Sales & Profit Analytics
        List<Sale> sales = saleRepository.findAllByTransactionDateBetween(startOfPeriod, endOfPeriod);
        long availableItems = productRepository.countByStatus(ProductStatusEnum.AVAILABLE);

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Sale sale : sales) {
            totalRevenue = totalRevenue.add(sale.getTotalAmount());
            for (SaleItem item : sale.getItems()) {
                BigDecimal itemCost = item.getProduct().getCostPrice();
                if (itemCost != null) {
                    totalCost = totalCost.add(itemCost);
                }
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalCost);

        // Old Gold Analytics
        List<OldGoldPurchase> oldGoldPurchases = oldGoldRepository.findAllByTransactionDateBetween(startOfPeriod, endOfPeriod);
        BigDecimal oldGoldBoughtWeight = BigDecimal.ZERO;
        BigDecimal oldGoldExpense = BigDecimal.ZERO;

        for (OldGoldPurchase purchase : oldGoldPurchases) {
            oldGoldBoughtWeight = oldGoldBoughtWeight.add(purchase.getWeight());
            oldGoldExpense = oldGoldExpense.add(purchase.getTotalValue());
        }

        // Purification Analytics
        List<ScrapPurification> purifications = purificationRepository.findAllByTransactionDateBetween(startOfPeriod, endOfPeriod);
        BigDecimal purificationIncome = purifications.stream()
                .map(ScrapPurification::getCashReceived)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Current Scrap Inventory
        List<ScrapInventory> scrapList = scrapInventoryRepository.findAll();
        Map<String, BigDecimal> scrapBoxMap = scrapList.stream()
                .collect(Collectors.toMap(s -> s.getPurity().name(), ScrapInventory::getTotalWeight));

        return new DashboardStatsDTO(
                totalRevenue,
                totalCost,
                netProfit,
                (long) sales.size(),
                availableItems,
                oldGoldBoughtWeight,
                oldGoldExpense,
                purificationIncome,
                scrapBoxMap,
                LocalDateTime.now(),
                startOfPeriod,
                endOfPeriod
        );
    }

    /**
     * Get top selling products for a date range
     */
    public List<TopProductDTO> getTopProducts(LocalDate fromDate, LocalDate toDate, int limit) {
        LocalDateTime startOfPeriod = fromDate.atStartOfDay();
        LocalDateTime endOfPeriod = toDate.atTime(LocalTime.MAX);

        List<Object[]> results = saleRepository.findTopProducts(startOfPeriod, endOfPeriod);

        return results.stream()
                .limit(limit)
                .map(row -> new TopProductDTO(
                        (String) row[0],           // productId
                        (String) row[1],           // barcode
                        (String) row[2],           // modelName
                        ((Number) row[3]).longValue(),  // salesCount
                        (BigDecimal) row[4],       // totalRevenue
                        (BigDecimal) row[5]        // totalWeight
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get user performance metrics for a date range
     */
    public List<UserPerformanceDTO> getUserPerformance(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startOfPeriod = fromDate.atStartOfDay();
        LocalDateTime endOfPeriod = toDate.atTime(LocalTime.MAX);

        List<Object[]> results = saleRepository.findUserPerformance(startOfPeriod, endOfPeriod);

        return results.stream()
                .map(row -> {
                    String username = (String) row[0];
                    Long salesCount = ((Number) row[1]).longValue();
                    BigDecimal totalRevenue = (BigDecimal) row[2];
                    BigDecimal averageSaleValue = salesCount > 0 
                            ? totalRevenue.divide(BigDecimal.valueOf(salesCount), 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    // Try to get user details
                    String fullName = username;
                    String userId = "";
                    try {
                        AppUser user = userRepository.findByUsername(username).orElse(null);
                        if (user != null) {
                            fullName = user.getFullName();
                            userId = user.getId();
                        }
                    } catch (Exception e) {
                        // If user not found, use username as fullName
                    }

                    return new UserPerformanceDTO(
                            userId,
                            username,
                            fullName,
                            salesCount,
                            totalRevenue,
                            averageSaleValue
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Get daily sales trends for a date range
     */
    public List<SalesTrendDTO> getSalesTrend(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime startOfPeriod = fromDate.atStartOfDay();
        LocalDateTime endOfPeriod = toDate.atTime(LocalTime.MAX);

        List<Object[]> results = saleRepository.findDailySalesTrend(startOfPeriod, endOfPeriod);

        // Get all sales for cost calculation
        List<Sale> allSales = saleRepository.findAllByTransactionDateBetween(startOfPeriod, endOfPeriod);
        Map<LocalDate, BigDecimal> costByDate = allSales.stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getTransactionDate().toLocalDate(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                sale -> sale.getItems().stream()
                                        .map(item -> item.getProduct().getCostPrice() != null 
                                                ? item.getProduct().getCostPrice() 
                                                : BigDecimal.ZERO)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                                BigDecimal::add
                        )
                ));

        return results.stream()
                .map(row -> {
                    LocalDate date = (LocalDate) row[0];
                    Long salesCount = ((Number) row[1]).longValue();
                    BigDecimal totalRevenue = (BigDecimal) row[2];
                    BigDecimal cost = costByDate.getOrDefault(date, BigDecimal.ZERO);
                    BigDecimal netProfit = totalRevenue.subtract(cost);

                    return new SalesTrendDTO(
                            date,
                            salesCount,
                            totalRevenue,
                            netProfit
                    );
                })
                .collect(Collectors.toList());
    }
}