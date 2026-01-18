package com.jewelry.pos.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "test", "default"})
public class StubHardwareService implements HardwareService {
    @Override
    public void openCashDrawer() {
        log.info("[SIMULATION] Drawer Opened (KA-CHING!)");
    }

    @Override
    public void printReceipt(String content) {
        log.info("[SIMULATION] Printing Receipt:\n{}", content);
    }
}