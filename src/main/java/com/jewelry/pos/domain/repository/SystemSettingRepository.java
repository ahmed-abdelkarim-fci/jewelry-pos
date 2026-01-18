package com.jewelry.pos.domain.repository;

import com.jewelry.pos.domain.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
    // Basic CRUD is enough
}