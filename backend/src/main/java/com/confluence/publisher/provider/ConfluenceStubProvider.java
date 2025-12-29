package com.confluence.publisher.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Stub implementation of BaseProvider for testing without making real API calls.
 */
@Slf4j
@Component
public class ConfluenceStubProvider implements BaseProvider {

    @Override
    public ProviderResult publishPage(String spaceKey, String title, String content, String parentPageId, List<String> attachmentPaths) {
        // Generate fake page ID
        String fakePageId = "CONF-" + UUID.randomUUID().toString().substring(0, 8);
        
        // Log operation details
        log.info("=== STUB PROVIDER: Publishing Page ===");
        log.info("Space Key: {}", spaceKey);
        log.info("Title: {}", title);
        log.info("Content Length: {} characters", content != null ? content.length() : 0);
        log.info("Parent Page ID: {}", parentPageId);
        log.info("Attachments: {}", attachmentPaths != null ? attachmentPaths.size() : 0);
        if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
            attachmentPaths.forEach(path -> log.info("  - {}", path));
        }
        log.info("Generated Page ID: {}", fakePageId);
        log.info("=====================================");
        
        // Return success result
        return new ProviderResult(
            fakePageId,
            "Successfully published to stub provider (no real API call made)"
        );
    }

    @Override
    public String getStatus(String confluencePageId) {
        log.info("STUB PROVIDER: Getting status for page ID: {}", confluencePageId);
        return "current";
    }
}

