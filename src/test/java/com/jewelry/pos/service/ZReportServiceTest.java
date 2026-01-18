package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Sale;
import com.jewelry.pos.domain.entity.SaleItem;
import com.jewelry.pos.domain.repository.SaleRepository;
import com.jewelry.pos.web.dto.ZReportDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZReportServiceTest {

    @Mock private SaleRepository saleRepository;
    @InjectMocks private ZReportService zReportService;

    @Test
    void generateReport_ShouldAggregateCorrectly() {
        // Sale 1: 10g sold for 1000
        Sale s1 = new Sale();
        s1.setTotalAmount(new BigDecimal("1000.00"));
        SaleItem i1 = new SaleItem();
        i1.setWeightSnapshot(new BigDecimal("10.000"));
        s1.addItem(i1);

        // Sale 2: 5g sold for 500
        Sale s2 = new Sale();
        s2.setTotalAmount(new BigDecimal("500.00"));
        SaleItem i2 = new SaleItem();
        i2.setWeightSnapshot(new BigDecimal("5.000"));
        s2.addItem(i2);

        when(saleRepository.findSalesBetween(any(), any())).thenReturn(List.of(s1, s2));

        // Execute
        ZReportDTO report = zReportService.generateEndOfDayReport(LocalDate.now());

        // Verify
        assertEquals(2, report.totalTransactions());
        assertEquals(new BigDecimal("1500.00"), report.totalRevenue());
        assertEquals(new BigDecimal("15.000"), report.totalGoldWeightSold());
    }
}