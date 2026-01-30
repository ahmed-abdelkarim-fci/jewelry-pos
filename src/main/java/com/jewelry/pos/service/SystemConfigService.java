package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.SystemSetting;
import com.jewelry.pos.domain.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemConfigService {

    private final SystemSettingRepository repository;

    // Define the Keys
    public static final String KEY_GOLD_AUTO_UPDATE = "gold_auto_update";
    public static final String KEY_HARDWARE_ENABLED = "hardware_enabled"; // <--- New Key

    // --- GOLD SETTINGS (Existing) ---
    public boolean isGoldAutoUpdateEnabled() {
        return getBoolean(KEY_GOLD_AUTO_UPDATE, false);
    }

    @Transactional
    public void setGoldAutoUpdate(boolean enabled) {
        setBoolean(KEY_GOLD_AUTO_UPDATE, enabled);
    }

    // --- HARDWARE SETTINGS (New) ---

    // Check if Hardware is ON (Default: TRUE)
    public boolean isHardwareEnabled() {
        // Default to TRUE because most POS systems expect hardware.
        // If they want manual mode, they must explicitly turn it OFF.
        return getBoolean(KEY_HARDWARE_ENABLED, true);
    }

    @Transactional
    public void setHardwareEnabled(boolean enabled) {
        setBoolean(KEY_HARDWARE_ENABLED, enabled);
    }

    // --- Helper Methods to reduce duplicate code ---

    private boolean getBoolean(String key, boolean defaultValue) {
        return repository.findById(key)
                .map(setting -> Boolean.parseBoolean(setting.getValue()))
                .orElse(defaultValue);
    }

    private void setBoolean(String key, boolean value) {
        SystemSetting setting = new SystemSetting(key, String.valueOf(value));
        repository.save(setting);
    }
}