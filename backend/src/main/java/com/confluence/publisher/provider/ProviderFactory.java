package com.confluence.publisher.provider;

/**
 * Factory for obtaining the appropriate PublishProvider implementation.
 */
public interface ProviderFactory {

    /**
     * Gets the configured PublishProvider instance.
     *
     * @return The active provider
     */
    PublishProvider getProvider();
}

