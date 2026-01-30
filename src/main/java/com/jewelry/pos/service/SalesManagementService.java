package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.Sale;
import com.jewelry.pos.domain.repository.SaleRepository;
import com.jewelry.pos.web.dto.SaleResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesManagementService {

    private final SaleRepository saleRepository;

    // 1. Get All Sales (Paged)
    public Page<SaleResponseDTO> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable)
                .map(this::mapToDTO);
    }

    // 2. Get Sale by ID
    public SaleResponseDTO getSaleById(String id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Sale not found"));
        return mapToDTO(sale);
    }

    // 3. Void Sale (Advanced: Requires refund logic)
    // For now, we will just delete the record. 
    // In a real system, you would mark status="VOIDED" and return items to inventory.
    @Transactional
    public void voidSale(String id) {
        if (!saleRepository.existsById(id)) {
            throw new IllegalStateException("Sale not found");
        }
        saleRepository.deleteById(id); 
    }

    private SaleResponseDTO mapToDTO(Sale sale) {
        var items = sale.getItems().stream()
                .map(item -> new SaleResponseDTO.SaleItemDTO(
                        item.getProduct().getModelName(),
                        item.getWeightSnapshot(),
                        item.getPriceSnapshot()

                ))
                .collect(Collectors.toList());

        return new SaleResponseDTO(
                sale.getId(),
                sale.getTransactionDate(),
                sale.getTotalAmount(),
                sale.getOldGoldTotalValue(),
                sale.getNetCashPaid(),
                sale.getCreatedBy(),
                sale.getCustomerName(),
                sale.getCustomerPhone(),
                items
        );
    }

    public Page<SaleResponseDTO> searchSales(
            String query, // Can be ID, Name, or Phone
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        Specification<Sale> spec = Specification.where(null);

        // 1. Text Filter (Matches ID OR Name OR Phone)
        if (query != null && !query.isBlank()) {
            String likePattern = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("id")), likePattern),
                    cb.like(cb.lower(root.get("customerName")), likePattern),
                    cb.like(root.get("customerPhone"), likePattern)
            ));
        }

        // 2. Date Filter (From Start of Day to End of Day)
        if (fromDate != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.greaterThanOrEqualTo(root.get("transactionDate"), fromDate.atStartOfDay())
            );
        }
        if (toDate != null) {
            spec = spec.and((root, cq, cb) ->
                    cb.lessThanOrEqualTo(root.get("transactionDate"), toDate.atTime(23, 59, 59))
            );
        }

        return saleRepository.findAll(spec, pageable)
                .map(this::mapToDTO);
    }

}