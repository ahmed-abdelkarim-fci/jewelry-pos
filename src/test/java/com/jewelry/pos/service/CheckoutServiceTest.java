package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.*;
import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.domain.repository.SaleRepository;
import com.jewelry.pos.web.dto.SaleRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private SaleRepository saleRepository;
    @Mock private HardwareService hardwareService;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void executeFinancialTransaction_ShouldCalculateCorrectly_AndCreateItem() {
        // 1. Setup Data
        Product mockProduct = new Product();
        mockProduct.setId("prod-123");
        mockProduct.setGrossWeight(new BigDecimal("10.000")); // 10g
        mockProduct.setMakingCharge(new BigDecimal("100.00")); // Flat fee

        SaleRequestDTO request = new SaleRequestDTO(
                List.of("12345"),
                new BigDecimal("3000.00"),
                "Test Customer",
                "1234567890",
                null
        );

        when(productRepository.findByBarcode("12345")).thenReturn(Optional.of(mockProduct));
        when(saleRepository.save(any(Sale.class))).thenAnswer(i -> {
            Sale s = i.getArgument(0);
            s.setId("sale-999"); // Simulate DB ID generation
            return s;
        });

        // 2. Execute
        Sale result = checkoutService.executeFinancialTransaction(request);

        // 3. Verify Math (FIX: Use compareTo to ignore scale differences)
        // Expected: (10g * 3000) + 100 = 30,100.00
        BigDecimal expectedTotal = new BigDecimal("30100.00");
        assertEquals(0, expectedTotal.compareTo(result.getTotalAmount()),
                "Total amount should match mathematically (ignoring scale)");

        // 4. Verify Item Creation
        assertFalse(result.getItems().isEmpty(), "Sale must contain items");
        SaleItem item = result.getItems().get(0);

        // Verify Item Snapshots
        assertEquals(0, new BigDecimal("10.000").compareTo(item.getWeightSnapshot()));
        assertEquals(0, expectedTotal.compareTo(item.getPriceSnapshot()));
        assertEquals(0, new BigDecimal("3000.00").compareTo(item.getAppliedGoldRate()));
    }

    @Test
    void processSale_ShouldTriggerHardware() {
        // 1. Create a SPY of the actual service
        CheckoutService spyService = spy(checkoutService);

        // FIX: Inject the SPY into itself.
        // This ensures that when 'processSale' calls 'self.executeFinancialTransaction',
        // it calls the SPY (which we can mock), not the original object.
        ReflectionTestUtils.setField(spyService, "self", spyService);

        // 2. Mock the DB part to return a dummy sale
        Sale mockSale = new Sale();
        mockSale.setId("sale-1");

        // "doReturn" is safer than "when" for spies to prevent side effects
        doReturn(mockSale).when(spyService).executeFinancialTransaction(any());

        // 3. Execute the method on the SPY
        spyService.processSale(new SaleRequestDTO(
                List.of("123"),
                BigDecimal.TEN,
                "Test Customer",
                null,
                null
        ));

        // 4. Verify Hardware was called
        verify(hardwareService, times(1)).openCashDrawer();
    }
}