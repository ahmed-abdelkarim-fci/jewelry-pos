package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.AppUser;
import com.jewelry.pos.domain.entity.Role;
import com.jewelry.pos.domain.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // 1. Get the currently logged-in user from Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 2. Find their details in the DB
        return userRepository.findByUsername(auth.getName())
            .map(user -> {
                // 3. Extract roles for the frontend
                Set<String> roles = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());
                
                // 4. Return a simple JSON profile
                return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "roles", roles,
                    "status", "Authenticated"
                ));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}