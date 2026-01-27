package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for selecting the appropriate Confluence provider.
 * This is a minimal stub that will be properly implemented in prompt 06.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {
    
    private final AppProperties appProperties;
    
    /**
     * Gets the configured provider.
     * Currently returns a stub implementation.
     * Will be properly implemented in prompt 06.
     * 
     * @return The configured provider
     */
    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider();
        log.warn("ProviderFactory.getProvider() called but providers not yet implemented. " +
                "Configured provider: {}. Returning null stub.", providerName);
        // Return a null stub for now - will be properly implemented in prompt 06
        return new BaseProvider() {
            @Override
            public ProviderResult publishPage(String spaceKey, String title, String content, 
                                             Long parentPageId, java.util.List<String> attachmentPaths) {
                log.warn("Stub provider called - providers not yet implemented");
                return new ProviderResult("STUB-PAGE-ID", "Stub implementation - providers not yet implemented");
            }
            
            @Override
            public String getStatus(String confluencePageId) {
                return "stub";
            }
        };
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
