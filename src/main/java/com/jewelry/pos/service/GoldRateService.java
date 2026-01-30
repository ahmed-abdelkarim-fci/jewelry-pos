package com.jewelry.pos.service;

import com.jewelry.pos.domain.entity.GoldRate;
import com.jewelry.pos.domain.entity.PurityEnum;
import com.jewelry.pos.domain.repository.GoldRateRepository;
import com.jewelry.pos.web.dto.GoldRateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoldRateService {

    private final GoldRateRepository goldRateRepository;

    @Transactional
    public void setDailyRate(GoldRateRequestDTO dto) {
        // Option A: Deactivate old rates (Audit trail style)
        // In a complex system, we'd mark old ones as inactive. 
        // For simplicity, we just insert the new "Effective" rate.
        
        GoldRate rate = new GoldRate();
        rate.setRate24k(dto.rate24k());
        rate.setRate21k(dto.rate21k());
        rate.setRate18k(dto.rate18k());
        rate.setEffectiveDate(LocalDateTime.now());
        rate.setActive(true);
        
        goldRateRepository.save(rate);
    }

    public Page<GoldRate> getRateHistory(Pageable pageable) {
        return goldRateRepository.findAll(pageable); // Defaults to sorting by ID or add Sort in Controller
    }
    
    public GoldRate getLatestRate() {
        return goldRateRepository.findTopByActiveTrueOrderByEffectiveDateDesc()
                .orElseThrow(() -> new IllegalStateException("No Gold Rate set for today."));
    }
    
    public BigDecimal getCurrentSellRateForPurity(PurityEnum purity) {
        GoldRate latestRate = getLatestRate();
        
        return switch (purity) {
            case K24 -> latestRate.getRate24k();
            case K21 -> latestRate.getRate21k();
            case K18 -> latestRate.getRate18k();
        };
    }
}