package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.provider.BaseProvider;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishService {

    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;

    @Transactional
    public PublishLog publishPage(Long pageId) {
        // Find page by ID
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
        // Get attachment file paths for the page
        List<PageAttachment> pageAttachments = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
        List<String> attachmentPaths = pageAttachments.stream()
                .map(pa -> {
                    Attachment attachment = attachmentRepository.findById(pa.getAttachmentId())
                            .orElse(null);
                    return attachment != null ? attachment.getStoragePath() : null;
                })
                .filter(path -> path != null)
                .collect(Collectors.toList());
        
        // Get provider from ProviderFactory
        BaseProvider provider = providerFactory.getProvider();
        if (provider == null) {
            throw new RuntimeException("Provider not available. Providers will be implemented in prompt 06.");
        }
        
        // Call provider.publishPage() with page data
        BaseProvider.ProviderResult result = provider.publishPage(
                page.getSpaceKey(),
                page.getTitle(),
                page.getContent(),
                page.getParentPageId(),
                attachmentPaths
        );
        
        // Create and save PublishLog with result
        PublishLog publishLog = PublishLog.builder()
                .pageId(pageId)
                .provider(providerFactory.getProviderName())
                .spaceKey(page.getSpaceKey())
                .confluencePageId(result.confluencePageId())
                .status("success")
                .message(result.message())
                .build();
        
        PublishLog saved = publishLogRepository.save(publishLog);
        log.info("Published page ID: {} to Confluence. Log ID: {}", pageId, saved.getId());
        return saved;
    }
}
