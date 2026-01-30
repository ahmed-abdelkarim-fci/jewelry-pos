package com.jewelry.pos.web.controller;

import com.jewelry.pos.domain.entity.AppUser;
import com.jewelry.pos.domain.entity.Role;
import com.jewelry.pos.domain.repository.UserRepository;
import com.jewelry.pos.security.JwtUtil;
import com.jewelry.pos.web.dto.LoginRequest;
import com.jewelry.pos.web.dto.LoginResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            AppUser user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String primaryRole = user.getRoles().stream()
                    .map(Role::getName)
                    .findFirst()
                    .orElse("ROLE_CASHIER");

            return ResponseEntity.ok(new LoginResponse(jwt, user.getUsername(), primaryRole));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        return userRepository.findByUsername(auth.getName())
            .map(user -> {
                Set<String> roles = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet());
                
                return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "fullName", user.getFullName(),
                    "enabled", user.isEnabled(),
                    "createdBy", user.getCreatedBy(),
                    "createdDate", user.getCreatedDate(),
                    "roles", roles,
                    "status", "Authenticated"
                ));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}