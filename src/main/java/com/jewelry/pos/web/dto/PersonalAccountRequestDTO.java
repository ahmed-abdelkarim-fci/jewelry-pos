package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.TransactionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PersonalAccountRequestDTO(
        @NotBlank(message = "{validation.personalAccount.personId.required}")
        String personId,
        
        @NotNull(message = "{validation.personalAccount.transactionDate.required}")
        LocalDateTime transactionDate,
        
        @NotBlank(message = "{validation.personalAccount.statement.required}")
        String statement,
        
        @NotNull(message = "{validation.personalAccount.transactionType.required}")
        TransactionTypeEnum transactionType,
        
        BigDecimal weight,
        BigDecimal money
) {}
