package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.TransactionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SupplierAccountRequestDTO(
        @NotBlank(message = "{validation.supplierAccount.supplierId.required}")
        String supplierId,
        
        @NotNull(message = "{validation.supplierAccount.transactionDate.required}")
        LocalDateTime transactionDate,
        
        String statement,
        
        @NotNull(message = "{validation.supplierAccount.transactionType.required}")
        TransactionTypeEnum transactionType,
        
        BigDecimal weight,
        BigDecimal fees,
        Integer numberOfPieces,
        String purificationId
) {}
