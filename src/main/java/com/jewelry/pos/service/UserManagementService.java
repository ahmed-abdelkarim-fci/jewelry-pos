package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.*;
import com.jewelry.pos.domain.repository.*;
import com.jewelry.pos.web.dto.CreateUserDTO;
import com.jewelry.pos.web.dto.UpdateUserDTO;
import com.jewelry.pos.web.dto.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. GET ALL (Paged)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // 2. GET BY ID OR USERNAME
    public UserResponseDTO getUserByIdOrUsername(String identifier) {
        // Try to find by ID first, then by Username
        AppUser user = userRepository.findById(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .orElseThrow(() -> new IllegalStateException("User not found with identifier: " + identifier));

        return mapToResponse(user);
    }

    // 3. UPDATE USER
    @Transactional
    public UserResponseDTO updateUser(String id, UpdateUserDTO dto) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Update fields if they are present in DTO
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.enabled() != null) user.setEnabled(dto.enabled());

        // Update Roles if provided
        if (dto.roles() != null && !dto.roles().isEmpty()) {
            Set<Role> newRoles = dto.roles().stream()
                    .map(name -> roleRepository.findByName(name)
                            .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
        }

        return mapToResponse(userRepository.save(user));
    }

    // 4. DELETE USER
    @Transactional
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalStateException("User not found");
        }
        // Prevent deleting the Super Admin or yourself (logic can be refined)
        // For now, simpler delete:
        userRepository.deleteById(id);
    }

    // --- USER CRUD ---
    @Transactional
    public AppUser createUser(CreateUserDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalStateException("Username already taken");
        }

        AppUser user = new AppUser();
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));

        // Assign Roles
        Set<Role> roles = dto.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        return userRepository.save(user);
    }

    // --- ROLE CRUD ---
    @Transactional
    public Role createRole(String roleName, Set<String> permissionNames) {
        if (roleRepository.findByName(roleName).isPresent()) {
            throw new IllegalStateException("Role exists");
        }

        Role role = new Role(roleName);

        // Fetch permissions from DB or create them? Usually fetch.
        if (permissionNames != null) {
            Set<Permission> perms = permissionNames.stream()
                    .map(p -> permissionRepository.findByName(p)
                            .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + p)))
                    .collect(Collectors.toSet());
            role.setPermissions(perms);
        }

        return roleRepository.save(role);
    }

    // --- SEEDER (Run once to create Super Admin) ---

    @Transactional
    public void seedInitialData() {
        if (userRepository.count() > 0) return; // Stop if data exists

        // ==========================================
        // 1. Create Permissions (P1, P2, P3)
        // ==========================================
        // P1: Top Level (User Management)
        Permission p1 = permissionRepository.save(new Permission("USER_MANAGE"));
        // P2: Mid Level (Inventory/Product Management)
        Permission p2 = permissionRepository.save(new Permission("PRODUCT_MANAGE"));
        // P3: Low Level (Sales Execution)
        Permission p3 = permissionRepository.save(new Permission("SALE_EXECUTE"));

        // ==========================================
        // 2. Create Roles with Cascading Permissions
        // ==========================================

        // Role 1: SUPER_ADMIN (Has P1, P2, P3)
        Role superAdminRole = new Role("ROLE_SUPER_ADMIN");
        superAdminRole.setPermissions(Set.of(p1, p2, p3));
        roleRepository.save(superAdminRole);

        // Role 2: ADMIN (Has P2, P3)
        Role adminRole = new Role("ROLE_ADMIN");
        adminRole.setPermissions(Set.of(p2, p3));
        roleRepository.save(adminRole);

        // Role 3: USER (Has P3 only)
        Role userRole = new Role("ROLE_USER");
        userRole.setPermissions(Set.of(p3));
        roleRepository.save(userRole);

        // ==========================================
        // 3. Create the Super Admin User
        // ==========================================
        // Note: passing Set.of("ROLE_SUPER_ADMIN") matches the role name above
        CreateUserDTO superAdminDto = new CreateUserDTO(
                "System",
                "Administrator",
                "super_admin",
                "super_admin123",
                Set.of("ROLE_SUPER_ADMIN")
        );

        // Reuse our existing createUser logic to handle password encoding & role lookup
        createUser(superAdminDto);
    }

    // Helper: Map Entity to Response DTO
    private UserResponseDTO mapToResponse(AppUser user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                roleNames,
                user.getCreatedDate(),
                user.getCreatedBy()
        );
    }
}