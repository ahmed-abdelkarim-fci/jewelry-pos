package com.jewelry.pos.web.dto;

import com.jewelry.pos.domain.entity.KaratEnum;
import com.jewelry.pos.domain.entity.PurityEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record OldGoldRequestDTO(

        @NotBlank(message = "{validation.oldGold.purity.required}")
        KaratEnum purity,

        @NotNull(message = "{validation.oldGold.weight.required}")
        @Positive(message = "{validation.oldGold.weight.positive}")
        BigDecimal weight,

        @NotNull(message = "{validation.oldGold.buyRate.required}")
        @Positive(message = "{validation.oldGold.buyRate.positive}")
        BigDecimal buyRate, // Price per gram

        String description, // Optional

        @NotBlank(message = "{validation.oldGold.nationalId.required}")
        String customerNationalId,

        String customerPhoneNumber
) {}