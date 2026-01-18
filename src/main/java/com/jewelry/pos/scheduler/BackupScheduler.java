package com.jewelry.pos.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class BackupScheduler {

    private final JdbcTemplate jdbcTemplate;
    private static final String BACKUP_DIR = "backups";

    // Cron: Runs every day at 2:00 PM and 9:00 PM (Shop closing/opening times)
    // Format: second, minute, hour, day, month, weekday
    @Scheduled(cron = "0 0 14,21 * * *") 
    public void performBackup() {
        log.info("Starting scheduled database backup...");
        
        try {
            // 1. Ensure directory exists
            Files.createDirectories(Paths.get(BACKUP_DIR));

            // 2. Generate Filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = BACKUP_DIR + "/auto_backup_" + timestamp + ".zip";

            // 3. Execute H2 Safe Backup Command
            jdbcTemplate.execute("BACKUP TO '" + fileName + "'");
            
            log.info("Backup successful: {}", fileName);

            // 4. Cleanup old backups (older than 7 days)
            cleanOldBackups();

        } catch (Exception e) {
            log.error("Scheduled backup failed!", e);
        }
    }

    private void cleanOldBackups() {
        try (Stream<Path> files = Files.list(Paths.get(BACKUP_DIR))) {
            files.filter(path -> path.toString().endsWith(".zip"))
                 .filter(path -> {
                     // Simple logic: Check file Last Modified Time
                     File file = path.toFile();
                     long diff = System.currentTimeMillis() - file.lastModified();
                     return diff > (7L * 24 * 60 * 60 * 1000); // 7 Days in milliseconds
                 })
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                         log.info("Deleted old backup: {}", path);
                     } catch (IOException e) {
                         log.error("Failed to delete old backup", e);
                     }
                 });
        } catch (IOException e) {
            log.error("Cleanup failed", e);
        }
    }
}