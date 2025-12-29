package com.confluence.publisher.provider;

import java.util.List;

/**
 * Base interface for publishing content to external systems (e.g., Confluence).
 */
public interface BaseProvider {

    /**
     * Result record returned by publish operations.
     *
     * @param confluencePageId The external page ID assigned by the provider
     * @param message Additional information about the publish operation
     */
    record ProviderResult(String confluencePageId, String message) {}

    /**
     * Publishes a page with attachments to the external system.
     *
     * @param spaceKey The target space key
     * @param title The page title
     * @param content The page content
     * @param parentPageId The parent page ID (optional)
     * @param attachmentPaths The list of attachment file paths
     * @return ProviderResult containing page ID and message
     */
    ProviderResult publishPage(String spaceKey, String title, String content, String parentPageId, List<String> attachmentPaths);

    /**
     * Gets the status of a published page.
     *
     * @param confluencePageId The external page ID
     * @return Status string
     */
    String getStatus(String confluencePageId);
}

