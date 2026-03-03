package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.GoldRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface GoldRateRepository extends JpaRepository<GoldRate, String> {
    // Find the most recent active rate
    Optional<GoldRate> findTopByActiveTrueOrderByEffectiveDateDesc();
    
    // Delete rates older than specified date
    void deleteByEffectiveDateBefore(LocalDateTime cutoffDate);
}