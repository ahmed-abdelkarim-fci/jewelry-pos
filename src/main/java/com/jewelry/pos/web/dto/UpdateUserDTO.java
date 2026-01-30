package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateUserDTO(
        @Size(min = 2, max = 50, message = "{validation.user.firstName.update.size}")
        String firstName,

        @Size(min = 2, max = 50, message = "{validation.user.lastName.update.size}")
        String lastName,

        Boolean enabled,
        Set<String> roles
) {}