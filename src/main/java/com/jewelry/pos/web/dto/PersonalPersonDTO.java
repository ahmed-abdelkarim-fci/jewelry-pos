package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PersonalPersonDTO(
        String id,

        @NotBlank(message = "{validation.personalPerson.name.required}")
        String name,

        @NotBlank(message = "{validation.personalPerson.phoneNumber.required}")
        String phoneNumber,

        @NotBlank(message = "{validation.personalPerson.address.required}")
        String address,
        String notes
) {}
