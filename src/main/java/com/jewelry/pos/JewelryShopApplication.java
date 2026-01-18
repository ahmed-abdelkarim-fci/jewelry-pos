package com.jewelry.pos;

import com.jewelry.pos.scheduler.GoldPriceScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
public class JewelryShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JewelryShopApplication.class, args);
	}

	@Bean
	CommandLineRunner testScraper(GoldPriceScheduler scheduler) {
		return args -> {
			System.out.println("--- TESTING SCRAPER START ---");
			scheduler.fetchGoldPrice();
			System.out.println("--- TESTING SCRAPER END ---");
		};
	}
}
