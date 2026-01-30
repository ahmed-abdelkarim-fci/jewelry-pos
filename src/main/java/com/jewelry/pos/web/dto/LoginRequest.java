package com.jewelry.pos.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "{validation.login.username.required}")
    private String username;
    
    @NotBlank(message = "{validation.login.password.required}")
    private String password;
}
