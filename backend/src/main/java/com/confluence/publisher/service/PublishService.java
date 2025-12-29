package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.provider.PublishProvider;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {

    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;

    @Transactional
    public PublishLog publishPage(Long pageId) {
        log.info("Publishing page with ID: {}", pageId);
        
        try {
            // Find page by ID
            Page page = pageRepository.findById(pageId)
                    .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
            
            // Get attachment file paths for the page
            List<PageAttachment> pageAttachments = pageAttachmentRepository
                    .findByPageIdOrderByPositionAsc(pageId);
            
            List<String> attachmentPaths = pageAttachments.stream()
                    .map(pa -> attachmentRepository.findById(pa.getAttachmentId())
                            .map(Attachment::getStoragePath)
                            .orElse(null))
                    .filter(path -> path != null)
                    .toList();
            
            log.debug("Found {} attachments for page {}", attachmentPaths.size(), pageId);
            
            // Get provider from ProviderFactory
            PublishProvider provider = providerFactory.getProvider();
            
            // Call provider.publishPage() with page data
            String confluencePageId = provider.publishPage(
                    page.getTitle(),
                    page.getContent(),
                    page.getSpaceKey(),
                    page.getParentPageId(),
                    attachmentPaths
            );
            
            log.info("Page {} published successfully with Confluence page ID: {}", pageId, confluencePageId);
            
            // Create and save PublishLog with result
            PublishLog publishLog = PublishLog.builder()
                    .pageId(pageId)
                    .provider(provider.getClass().getSimpleName())
                    .spaceKey(page.getSpaceKey())
                    .confluencePageId(confluencePageId)
                    .status("success")
                    .message("Page published successfully")
                    .build();
            
            return publishLogRepository.save(publishLog);
            
        } catch (Exception e) {
            log.error("Failed to publish page {}: {}", pageId, e.getMessage(), e);
            
            // Create and save PublishLog with error
            PublishLog publishLog = PublishLog.builder()
                    .pageId(pageId)
                    .provider("unknown")
                    .status("error")
                    .message("Failed to publish: " + e.getMessage())
                    .build();
            
            return publishLogRepository.save(publishLog);
        }
    }
}

