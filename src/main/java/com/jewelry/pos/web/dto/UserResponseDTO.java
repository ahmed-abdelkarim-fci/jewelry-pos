package com.jewelry.pos.web.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponseDTO(
    String id,
    String username,
    String firstName,
    String lastName,
    boolean enabled,
    Set<String> roles,
    LocalDateTime createdDate,
    String createdBy
) {}