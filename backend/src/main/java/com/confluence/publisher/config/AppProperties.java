package com.confluence.publisher.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private String appName = "confluence-publisher";
    private String databaseUrl = "jdbc:sqlite:./data/app.db";
    private String attachmentDir = "storage/attachments";
    private String confluenceUrl = "https://your-domain.atlassian.net";
    private String confluenceUsername = "";
    private String confluenceDefaultSpace = "DEV";
    private String confluenceApiToken = "";
    
    @Setter(AccessLevel.NONE)
    private List<String> corsOrigins = new ArrayList<>(Arrays.asList(
        "http://localhost:4200",
        "http://localhost:8080",
        "http://localhost:5173"
    ));
    
    private String provider = "confluence-server";
    private Integer schedulerIntervalSeconds = 5;
    
    /**
     * Setter for List<String> - used when binding from YAML array
     */
    public void setCorsOrigins(List<String> corsOrigins) {
        this.corsOrigins = corsOrigins != null ? new ArrayList<>(corsOrigins) : new ArrayList<>();
    }
    
    /**
     * Setter that can parse comma-separated CORS origins from environment variable.
     * This allows setting CORS_ORIGINS="http://localhost:4200,http://localhost:8080"
     */
    public void setCorsOrigins(String corsOriginsString) {
        if (corsOriginsString != null && !corsOriginsString.trim().isEmpty()) {
            this.corsOrigins = Arrays.stream(corsOriginsString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        }
    }
}
