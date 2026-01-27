package com.confluence.publisher.provider;

import java.util.List;

/**
 * Base interface for Confluence providers.
 * This is a minimal stub that will be properly implemented in prompt 06.
 */
public interface BaseProvider {
    
    /**
     * Publishes a page to Confluence.
     * 
     * @param spaceKey The Confluence space key
     * @param title The page title
     * @param content The page content
     * @param parentPageId Optional parent page ID
     * @param attachmentPaths List of attachment file paths
     * @return ProviderResult with confluence page ID and message
     */
    ProviderResult publishPage(String spaceKey, String title, String content, 
                               Long parentPageId, List<String> attachmentPaths);
    
    /**
     * Gets the status of a published page.
     * 
     * @param confluencePageId The Confluence page ID
     * @return Status string
     */
    String getStatus(String confluencePageId);
}
