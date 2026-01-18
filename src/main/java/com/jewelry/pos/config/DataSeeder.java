package com.jewelry.pos.config;

import com.jewelry.pos.service.UserManagementService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initDatabase(UserManagementService userService) {
        return args -> {
            userService.seedInitialData();
            System.out.println("--- Database Seeded with Admin User ---");
        };
    }
}