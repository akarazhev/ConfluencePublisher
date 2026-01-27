package com.confluence.publisher.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ConfluenceStubProvider implements BaseProvider {

    @Override
    public ProviderResult publishPage(String spaceKey, String title, String content, Long parentPageId, List<String> attachmentPaths) {
        // Generate fake page ID like "CONF-" + random UUID substring
        String fakePageId = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        log.info("STUB: Publishing page to Confluence (stub mode)");
        log.info("  Space Key: {}", spaceKey);
        log.info("  Title: {}", title);
        log.info("  Content length: {} characters", content != null ? content.length() : 0);
        log.info("  Parent Page ID: {}", parentPageId);
        log.info("  Attachments: {}", attachmentPaths != null ? attachmentPaths.size() : 0);
        log.info("  Generated fake Confluence Page ID: {}", fakePageId);
        
        String message = String.format("Page published successfully (stub mode). Fake ID: %s", fakePageId);
        return new ProviderResult(fakePageId, message);
    }

    @Override
    public String getStatus(String confluencePageId) {
        log.info("STUB: Getting status for Confluence page ID: {}", confluencePageId);
        return "current";
    }
}
