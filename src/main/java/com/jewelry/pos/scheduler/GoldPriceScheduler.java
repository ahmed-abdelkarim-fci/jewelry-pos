package com.jewelry.pos.scheduler;

import com.jewelry.pos.service.GoldRateService;
import com.jewelry.pos.service.SystemConfigService;
import com.jewelry.pos.web.dto.GoldRateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoldPriceScheduler {

    private final GoldRateService goldRateService;
    private final SystemConfigService configService;

    // ✅ CORRECTED URL (Tested & Valid)
    private static final String SCRAPE_URL = "https://egypt.gold-price-today.com/";

    // Run every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void fetchGoldPrice() {
        if (!configService.isGoldAutoUpdateEnabled()) {
            return;
        }

        log.info("Connecting to Gold Price Source: {}", SCRAPE_URL);

        try {
            // 1. Connect with a standard User-Agent to avoid blocking
            Document doc = Jsoup.connect(SCRAPE_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000) // Increase timeout to 10s for slow internet
                    .get();

            // 2. Extract 24k Price
            // This site lists "Sell" and "Buy" prices. We usually want the Higher price (Selling to customer).
            BigDecimal price24k = extractPriceFromRow(doc, "24");

            if (price24k != null) {
                // 3. Calculate other Karats (Math is safer than scraping individually)
                BigDecimal price21k = price24k.multiply(new BigDecimal("0.875")); // 21 / 24
                BigDecimal price18k = price24k.multiply(new BigDecimal("0.750")); // 18 / 24

                GoldRateRequestDTO dto = new GoldRateRequestDTO(price24k, price21k, price18k);
                goldRateService.setDailyRate(dto);

                log.info("✅ Gold Price Updated: 24k = {} EGP", price24k.intValue());
            } else {
                log.warn("⚠️ Content found, but could not identify 24k price row.");
            }

        } catch (Exception e) {
            log.error("❌ Scrape failed: {}. Keeping old price.", e.getMessage());
        }
    }

    private BigDecimal extractPriceFromRow(Document doc, String keyword) {
        // Get all table rows
        Elements rows = doc.select("tr");

        for (Element row : rows) {
            String text = row.text();
            // Look for "24" (English) or "٢٤" (Arabic) inside the row
            if (text.contains(keyword) || text.contains("٢٤")) {

                // Extract all possible numbers from this row
                List<BigDecimal> numbers = new ArrayList<>();
                String[] parts = text.split(" ");

                for (String part : parts) {
                    // Remove non-numeric chars (keep digits and dots)
                    String clean = part.replaceAll("[^0-9.]", "");
                    if (!clean.isEmpty()) {
                        try {
                            // Filter out small numbers (like "24" itself)
                            BigDecimal val = new BigDecimal(clean);
                            if (val.doubleValue() > 1000) { // Price must be > 1000 EGP
                                numbers.add(val);
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }

                // If we found numbers (e.g., Buy Price and Sell Price)
                if (!numbers.isEmpty()) {
                    // Return the MAX value (Selling Price is usually the higher one)
                    return Collections.max(numbers);
                }
            }
        }
        return null;
    }
}