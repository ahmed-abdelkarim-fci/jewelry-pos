package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateUserDTO(
        @NotBlank(message = "{validation.user.firstName.required}")
        @Size(min = 2, max = 50, message = "{validation.user.firstName.size}")
        String firstName,

        @NotBlank(message = "{validation.user.lastName.required}")
        @Size(min = 2, max = 50, message = "{validation.user.lastName.size}")
        String lastName,

        @NotBlank(message = "{validation.user.username.required}")
        @Size(min = 3, max = 20, message = "{validation.user.username.size}")
        @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "{validation.user.username.pattern}")
        String username,

        @NotBlank(message = "{validation.user.password.required}")
        @Size(min = 6, max = 100, message = "{validation.user.password.size}")
        String password,

        @NotEmpty(message = "{validation.user.roles.required}")
        Set<String> roles
) {}