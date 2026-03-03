package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotBlank;

public record SupplierDTO(
        String id,
        
        @NotBlank(message = "{validation.supplier.name.required}")
        String name,
        
        String phoneNumber,
        String address,
        String notes
) {}
