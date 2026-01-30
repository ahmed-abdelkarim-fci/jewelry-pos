package com.jewelry.pos.config;

import com.jewelry.pos.domain.entity.GoldRate;
import com.jewelry.pos.domain.repository.GoldRateRepository;
import com.jewelry.pos.service.UserManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initDatabase(UserManagementService userService, GoldRateRepository goldRateRepository) {
        return args -> {
            userService.seedInitialData();
            System.out.println("--- Database Seeded with Admin User ---");
            
            // Seed initial gold rates if none exist
            if (goldRateRepository.count() == 0) {
                GoldRate initialRate = new GoldRate();
                initialRate.setRate24k(new BigDecimal("4000.00"));
                initialRate.setRate21k(new BigDecimal("3500.00"));
                initialRate.setRate18k(new BigDecimal("3000.00"));
                initialRate.setEffectiveDate(LocalDateTime.now());
                initialRate.setActive(true);
                goldRateRepository.save(initialRate);
                System.out.println("--- Database Seeded with Initial Gold Rates ---");
            }
        };
    }
}