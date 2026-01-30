package com.jewelry.pos.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecentTransactionDTO(
        String id,
        LocalDateTime date,
        String type,
        BigDecimal amount,
        String description
) {}
