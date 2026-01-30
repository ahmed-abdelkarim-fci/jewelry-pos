package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Sale;
import com.jewelry.pos.domain.entity.SaleItem;
import com.jewelry.pos.domain.repository.SaleRepository;
import com.jewelry.pos.web.dto.RecentTransactionDTO;
import com.jewelry.pos.web.dto.ZReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZReportService {

    private final SaleRepository saleRepository;

    @Transactional(readOnly = true)
    public ZReportDTO generateEndOfDayReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Sale> sales = saleRepository.findSalesBetween(start, end);

        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate total weight (now works because CheckoutService creates items)
        BigDecimal totalWeight = sales.stream()
                .flatMap(s -> s.getItems().stream())
                .map(SaleItem::getWeightSnapshot)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ZReportDTO(date, sales.size(), totalRevenue, totalWeight);
    }

    @Transactional(readOnly = true)
    public List<RecentTransactionDTO> getRecentTransactions() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return saleRepository.findSalesBetween(thirtyDaysAgo, LocalDateTime.now())
                .stream()
                .map(sale -> new RecentTransactionDTO(
                        sale.getId(),
                        sale.getTransactionDate(),
                        "SALE",
                        sale.getTotalAmount(),
                        sale.getCustomerName()
                ))
                .toList();
    }
}