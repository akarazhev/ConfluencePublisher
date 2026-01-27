package com.confluence.publisher.provider;

/**
 * Result record for provider operations.
 */
public record ProviderResult(String confluencePageId, String message) {
}
