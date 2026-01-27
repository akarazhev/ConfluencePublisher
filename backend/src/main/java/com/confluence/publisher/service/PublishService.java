package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.provider.BaseProvider;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.provider.ProviderResult;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublishService {
    
    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;
    
    @Transactional
    public PublishLog publishPage(Long pageId) {
        log.info("Publishing page with ID: {}", pageId);
        
        // Find page by ID
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
        // Get attachment file paths for the page
        List<PageAttachment> pageAttachments = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
        List<String> attachmentPaths = new ArrayList<>();
        
        for (PageAttachment pageAttachment : pageAttachments) {
            Attachment attachment = attachmentRepository.findById(pageAttachment.getAttachmentId())
                    .orElse(null);
            
            if (attachment != null && attachment.getStoragePath() != null) {
                attachmentPaths.add(attachment.getStoragePath());
                log.debug("Added attachment path: {}", attachment.getStoragePath());
            }
        }
        
        // Get provider from ProviderFactory
        BaseProvider provider = providerFactory.getProvider();
        String providerName = providerFactory.getProviderName();
        
        // Call provider.publishPage() with page data
        ProviderResult result;
        try {
            result = provider.publishPage(
                    page.getSpaceKey(),
                    page.getTitle(),
                    page.getContent(),
                    page.getParentPageId(),
                    attachmentPaths
            );
            log.info("Provider published page successfully. Confluence page ID: {}, message: {}", 
                    result.confluencePageId(), result.message());
        } catch (Exception e) {
            log.error("Error publishing page via provider", e);
            result = new ProviderResult(null, "Error: " + e.getMessage());
        }
        
        // Create and save PublishLog with result
        String publishStatus = result.confluencePageId() != null ? "success" : "failed";
        PublishLog publishLog = PublishLog.builder()
                .pageId(pageId)
                .provider(providerName)
                .spaceKey(page.getSpaceKey())
                .confluencePageId(result.confluencePageId())
                .status(publishStatus)
                .message(result.message())
                .build();
        
        PublishLog savedLog = publishLogRepository.save(publishLog);
        log.info("PublishLog created with ID: {}, status: {}", savedLog.getId(), publishStatus);
        
        // Return the publish log
        return savedLog;
    }
}
