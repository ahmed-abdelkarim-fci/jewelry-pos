package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.TransactionTypeEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HomeExpenseRequestDTO(
        @NotNull(message = "{validation.homeExpense.transactionDate.required}")
        LocalDateTime transactionDate,
        
        String description,
        
        @NotNull(message = "{validation.homeExpense.transactionType.required}")
        TransactionTypeEnum transactionType,
        
        BigDecimal weight,
        BigDecimal money
) {}
