package com.confluence.publisher.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private String appName = "confluence-publisher";
    private String databaseUrl = "jdbc:sqlite:./data/app.db";
    private String attachmentDir = "storage/attachments";
    private String confluenceUrl = "https://your-domain.atlassian.net";
    private String confluenceUsername = "";
    private String confluenceDefaultSpace = "DEV";
    private String confluenceApiToken = "";
    private List<String> corsOrigins = Arrays.asList(
            "http://localhost:4200",
            "http://localhost:8080",
            "http://localhost:5173"
    );
    private String provider = "confluence-server";
    private Integer schedulerIntervalSeconds = 5;
    
    /**
     * Setter that can parse comma-separated CORS origins from environment variable.
     * Example: "http://localhost:4200,http://localhost:8080,http://localhost:5173"
     */
    public void setCorsOrigins(String corsOriginsString) {
        if (corsOriginsString != null && !corsOriginsString.trim().isEmpty()) {
            this.corsOrigins = Arrays.stream(corsOriginsString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
    }
    
    public void setCorsOrigins(List<String> corsOrigins) {
        this.corsOrigins = corsOrigins;
    }
}

