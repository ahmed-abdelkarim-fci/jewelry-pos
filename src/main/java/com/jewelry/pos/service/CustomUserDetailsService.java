package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.AppUser;
import com.jewelry.pos.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert Roles & Permissions -> GrantedAuthorities
        Set<GrantedAuthority> authorities = appUser.getRoles().stream()
                .flatMap(role -> {
                    // Add the Role itself (e.g., "ROLE_ADMIN")
                    Set<GrantedAuthority> auths = new java.util.HashSet<>();
                    auths.add(new SimpleGrantedAuthority(role.getName()));
                    
                    // Add all permissions within that Role (e.g., "PRODUCT_DELETE")
                    role.getPermissions().forEach(p -> 
                        auths.add(new SimpleGrantedAuthority(p.getName()))
                    );
                    return auths.stream();
                })
                .collect(Collectors.toSet());

        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                appUser.isEnabled(),
                true, true, true,
                authorities
        );
    }
}