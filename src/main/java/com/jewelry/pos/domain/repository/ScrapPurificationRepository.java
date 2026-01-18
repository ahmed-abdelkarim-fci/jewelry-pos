package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.ScrapPurification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScrapPurificationRepository extends JpaRepository<ScrapPurification, String> {
    // Add this method inside the interface
    List<ScrapPurification> findAllByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
}