package com.confluence.publisher.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final AppProperties appProperties;
    
    @PostConstruct
    public void init() {
        try {
            // Create the database directory if it doesn't exist
            String databaseUrl = appProperties.getDatabaseUrl();
            if (databaseUrl.startsWith("jdbc:sqlite:")) {
                String dbPath = databaseUrl.replace("jdbc:sqlite:", "");
                Path dbFilePath = Paths.get(dbPath);
                Path dbDirectory = dbFilePath.getParent();
                
                if (dbDirectory != null && !Files.exists(dbDirectory)) {
                    Files.createDirectories(dbDirectory);
                    log.info("Created database directory: {}", dbDirectory.toAbsolutePath());
                }
            }
            
            // Create the attachment directory if it doesn't exist
            Path attachmentPath = Paths.get(appProperties.getAttachmentDir());
            if (!Files.exists(attachmentPath)) {
                Files.createDirectories(attachmentPath);
                log.info("Created attachment directory: {}", attachmentPath.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Failed to initialize directories", e);
            throw new RuntimeException("Failed to initialize directories", e);
        }
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Data initialization completed successfully");
        log.info("Database URL: {}", appProperties.getDatabaseUrl());
        log.info("Attachment directory: {}", appProperties.getAttachmentDir());
        log.info("Confluence URL: {}", appProperties.getConfluenceUrl());
        log.info("Confluence default space: {}", appProperties.getConfluenceDefaultSpace());
        log.info("Provider: {}", appProperties.getProvider());
        log.info("Scheduler interval (seconds): {}", appProperties.getSchedulerIntervalSeconds());
        log.info("CORS origins: {}", appProperties.getCorsOrigins());
    }
}

