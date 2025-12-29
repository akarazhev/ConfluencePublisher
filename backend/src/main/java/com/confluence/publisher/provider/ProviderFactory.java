package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Factory for obtaining the appropriate BaseProvider implementation based on configuration.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {

    private final AppProperties appProperties;
    private final ConfluenceStubProvider stubProvider;
    private final ConfluenceServerProvider serverProvider;

    /**
     * Gets the configured BaseProvider instance based on app.provider configuration.
     *
     * @return The active provider
     */
    public BaseProvider getProvider() {
        String providerName = appProperties.getProvider();
        
        if (providerName == null) {
            log.warn("No provider configured, falling back to stub provider");
            return stubProvider;
        }

        return switch (providerName.toLowerCase()) {
            case "confluence-server", "server" -> {
                log.info("Using Confluence Server provider");
                yield serverProvider;
            }
            case "confluence-stub", "stub" -> {
                log.info("Using Confluence Stub provider");
                yield stubProvider;
            }
            default -> {
                log.warn("Unknown provider '{}', falling back to stub provider", providerName);
                yield stubProvider;
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

