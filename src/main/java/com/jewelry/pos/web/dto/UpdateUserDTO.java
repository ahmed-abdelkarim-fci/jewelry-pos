package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateUserDTO(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        Boolean enabled,
        Set<String> roles
) {}