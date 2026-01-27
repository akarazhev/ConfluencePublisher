package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {
    
    private final AppProperties appProperties;
    
    @Bean
    @Primary
    public DataSource dataSource() {
        String url = appProperties.getDatabaseUrl();
        
        // Handle both jdbc:sqlite: and jdbc:sqlite:/// URL formats
        // Normalize to jdbc:sqlite: format if needed
        if (url.startsWith("jdbc:sqlite:///")) {
            url = url.replace("jdbc:sqlite:///", "jdbc:sqlite:");
        }
        
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl(url);
        
        return dataSource;
    }
}
