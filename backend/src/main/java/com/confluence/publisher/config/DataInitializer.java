package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppProperties appProperties;

    @Override
    public void run(String... args) throws Exception {
        // Create database directory if it doesn't exist
        String databaseUrl = appProperties.getDatabaseUrl();
        Path dbPath;
        
        if (databaseUrl.startsWith("jdbc:sqlite:///")) {
            // Extract path from jdbc:sqlite:///path/to/db
            String path = databaseUrl.substring("jdbc:sqlite:///".length());
            dbPath = Paths.get(path).getParent();
        } else if (databaseUrl.startsWith("jdbc:sqlite:")) {
            // Extract path from jdbc:sqlite:path/to/db
            String path = databaseUrl.substring("jdbc:sqlite:".length());
            dbPath = Paths.get(path).getParent();
        } else {
            // Fallback
            dbPath = Paths.get("./data");
        }
        
        if (dbPath != null) {
            Files.createDirectories(dbPath);
            log.info("Database directory initialized: {}", dbPath.toAbsolutePath());
        }
        
        // Create attachment directory if it doesn't exist
        Path attachmentPath = Paths.get(appProperties.getAttachmentDir());
        Files.createDirectories(attachmentPath);
        log.info("Attachment directory initialized: {}", attachmentPath.toAbsolutePath());
        
        log.info("Data initialization completed");
    }
}
