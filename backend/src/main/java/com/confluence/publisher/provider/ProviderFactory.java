package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory to select provider based on configuration.
 * This is a minimal stub implementation that will be properly implemented in prompt 06.
 * For now, it returns null and logs a warning.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {

    private final AppProperties appProperties;

    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider();
        log.warn("ProviderFactory.getProvider() called but providers not yet implemented. " +
                "Configured provider: {}. This will be implemented in prompt 06.", providerName);
        // Return null for now - this will cause PublishService to fail, but the structure is correct
        // This will be properly implemented in prompt 06
        return null;
    }

    public String getProviderName() {
        return appProperties.getProvider();
    }
}
