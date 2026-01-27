package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for selecting the appropriate Confluence provider based on configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {
    
    private final AppProperties appProperties;
    private final ConfluenceStubProvider stubProvider;
    private final ConfluenceServerProvider serverProvider;
    
    /**
     * Gets the configured provider based on app.provider configuration.
     * 
     * Supported values:
     * - "confluence-server" or "server" → ConfluenceServerProvider
     * - "confluence-stub" or "stub" → ConfluenceStubProvider
     * - Unknown → falls back to stub with warning
     * 
     * @return The configured provider
     */
    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider();
        if (providerName == null || providerName.trim().isEmpty()) {
            log.warn("No provider configured, defaulting to stub");
            return stubProvider;
        }
        
        String normalized = providerName.toLowerCase().trim();
        
        if ("confluence-server".equals(normalized) || "server".equals(normalized)) {
            log.debug("Using ConfluenceServerProvider");
            return serverProvider;
        } else if ("confluence-stub".equals(normalized) || "stub".equals(normalized)) {
            log.debug("Using ConfluenceStubProvider");
            return stubProvider;
        } else {
            log.warn("Unknown provider '{}', falling back to stub provider", providerName);
            return stubProvider;
        }
    }
    
    /**
     * Gets the configured provider name.
     * 
     * @return The provider name from configuration
     */
    public String getProviderName() {
        return appProperties.getProvider();
    }
}
