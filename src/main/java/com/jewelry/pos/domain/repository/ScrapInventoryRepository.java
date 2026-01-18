package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.ScrapInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapInventoryRepository extends JpaRepository<ScrapInventory, String> {
}