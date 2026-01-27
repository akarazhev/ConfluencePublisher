package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        if (databaseUrl.startsWith("jdbc:sqlite:")) {
            String dbPath = databaseUrl.substring("jdbc:sqlite:".length());
            Path dbFile = Paths.get(dbPath);
            Path dbDir = dbFile.getParent();
            
            if (dbDir != null && !Files.exists(dbDir)) {
                Files.createDirectories(dbDir);
                log.info("Created database directory: {}", dbDir);
            }
        }
        
        // Create attachment directory if it doesn't exist
        Path attachmentDir = Paths.get(appProperties.getAttachmentDir());
        if (!Files.exists(attachmentDir)) {
            Files.createDirectories(attachmentDir);
            log.info("Created attachment directory: {}", attachmentDir);
        }
        
        log.info("Data initialization completed");
    }
}
