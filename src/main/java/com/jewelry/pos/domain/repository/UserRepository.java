package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}