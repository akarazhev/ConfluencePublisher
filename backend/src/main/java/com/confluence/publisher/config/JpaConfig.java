package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JpaConfig {
    
    private final AppProperties appProperties;
    
    @Bean
    public DataSource dataSource() {
        // Create the database directory before initializing the DataSource
        String url = appProperties.getDatabaseUrl();
        if (url.startsWith("jdbc:sqlite:")) {
            try {
                String dbPath = url.replace("jdbc:sqlite:", "");
                Path dbFilePath = Paths.get(dbPath);
                Path dbDirectory = dbFilePath.getParent();
                
                if (dbDirectory != null && !Files.exists(dbDirectory)) {
                    Files.createDirectories(dbDirectory);
                    log.info("Created database directory: {}", dbDirectory.toAbsolutePath());
                }
            } catch (Exception e) {
                log.error("Failed to create database directory", e);
                throw new RuntimeException("Failed to create database directory", e);
            }
        }
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        
        // Handle both jdbc:sqlite: and jdbc:sqlite:/// URL formats
        if (url.startsWith("jdbc:sqlite:") && !url.startsWith("jdbc:sqlite:///")) {
            // Normalize the URL if it doesn't have triple slashes
            url = url.replace("jdbc:sqlite:", "jdbc:sqlite:");
        }
        dataSource.setUrl(url);
        
        return dataSource;
    }
}

