package com.confluence.publisher.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final AppProperties appProperties;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        
        // Handle both jdbc:sqlite: and jdbc:sqlite:/// URL formats
        String url = appProperties.getDatabaseUrl();
        if (url.startsWith("jdbc:sqlite:///")) {
            // jdbc:sqlite:///path/to/db format - use as is
            dataSource.setUrl(url);
        } else if (url.startsWith("jdbc:sqlite:")) {
            // jdbc:sqlite:path/to/db format - use as is
            dataSource.setUrl(url);
        } else {
            // Fallback: prepend jdbc:sqlite: if missing
            dataSource.setUrl("jdbc:sqlite:" + url);
        }
        
        return dataSource;
    }
}
