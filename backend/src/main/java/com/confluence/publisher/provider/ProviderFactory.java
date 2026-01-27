package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory to select provider based on configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {

    private final AppProperties appProperties;
    private final ConfluenceStubProvider confluenceStubProvider;
    private final ConfluenceServerProvider confluenceServerProvider;

    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider();
        
        if (providerName == null || providerName.isBlank()) {
            log.warn("No provider configured, falling back to stub provider");
            return confluenceStubProvider;
        }
        
        String normalizedProvider = providerName.toLowerCase().trim();
        
        if ("confluence-server".equals(normalizedProvider) || "server".equals(normalizedProvider)) {
            log.info("Using ConfluenceServerProvider");
            return confluenceServerProvider;
        } else if ("confluence-stub".equals(normalizedProvider) || "stub".equals(normalizedProvider)) {
            log.info("Using ConfluenceStubProvider");
            return confluenceStubProvider;
        } else {
            log.warn("Unknown provider '{}', falling back to stub provider", providerName);
            return confluenceStubProvider;
        }
    }

    public String getProviderName() {
        return appProperties.getProvider();
    }
}
