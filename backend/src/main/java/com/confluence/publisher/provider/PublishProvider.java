package com.confluence.publisher.provider;

import java.util.List;

/**
 * Interface for publishing content to external systems (e.g., Confluence).
 */
public interface PublishProvider {

    /**
     * Publishes a page with attachments to the external system.
     *
     * @param title The page title
     * @param content The page content
     * @param spaceKey The target space key
     * @param parentPageId The parent page ID (optional)
     * @param attachmentPaths The list of attachment file paths
     * @return The external page ID assigned by the provider
     */
    String publishPage(String title, String content, String spaceKey, Long parentPageId, List<String> attachmentPaths);
}

