package com.confluence.publisher.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ConfluenceStubProvider implements BaseProvider {

    @Override
    public ProviderResult publishPage(String spaceKey, String title, String content,
                                      Long parentPageId, List<String> attachmentPaths) {
        String fakePageId = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        log.info("Stub provider: Publishing page to space '{}' with title '{}'", spaceKey, title);
        log.debug("Stub provider: Content length: {} chars, Parent page ID: {}, Attachments: {}",
                content != null ? content.length() : 0, parentPageId, 
                attachmentPaths != null ? attachmentPaths.size() : 0);
        
        if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
            log.info("Stub provider: Would upload {} attachment(s)", attachmentPaths.size());
        }
        
        String message = String.format("Stub: Page '%s' published to space '%s' (fake ID: %s)",
                title, spaceKey, fakePageId);
        
        return new ProviderResult(fakePageId, message);
    }

    @Override
    public String getStatus(String confluencePageId) {
        log.info("Stub provider: Getting status for page ID: {}", confluencePageId);
        return "current";
    }
}
