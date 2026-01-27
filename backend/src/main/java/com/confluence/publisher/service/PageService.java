package com.confluence.publisher.service;

import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
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
public class PageService {
    
    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    
    @Transactional
    public Page createPage(String title, String content, String spaceKey, 
                          Long parentPageId, List<Long> attachmentIds) {
        log.info("Creating page with title: {}, spaceKey: {}, parentPageId: {}, attachments: {}", 
                title, spaceKey, parentPageId, attachmentIds);
        
        // Create and save the page
        Page page = Page.builder()
                .title(title)
                .content(content)
                .spaceKey(spaceKey)
                .parentPageId(parentPageId)
                .build();
        
        Page savedPage = pageRepository.save(page);
        log.debug("Page created with ID: {}", savedPage.getId());
        
        // Create PageAttachment records for each attachment ID with position index
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (int i = 0; i < attachmentIds.size(); i++) {
                Long attachmentId = attachmentIds.get(i);
                
                // Verify attachment exists
                if (!attachmentRepository.existsById(attachmentId)) {
                    log.warn("Attachment with ID {} does not exist, skipping", attachmentId);
                    continue;
                }
                
                PageAttachment pageAttachment = PageAttachment.builder()
                        .pageId(savedPage.getId())
                        .attachmentId(attachmentId)
                        .position(i)
                        .build();
                
                pageAttachmentRepository.save(pageAttachment);
                log.debug("Created PageAttachment for pageId: {}, attachmentId: {}, position: {}", 
                        savedPage.getId(), attachmentId, i);
            }
        }
        
        return savedPage;
    }
    
    public PageResponse getPage(Long pageId) {
        log.debug("Getting page with ID: {}", pageId);
        
        // Find page by ID or throw exception
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
        // Load associated attachments via PageAttachmentRepository
        List<PageAttachment> pageAttachments = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
        
        // Build attachment info list
        List<PageResponse.AttachmentInfo> attachmentInfos = new ArrayList<>();
        for (PageAttachment pageAttachment : pageAttachments) {
            Attachment attachment = attachmentRepository.findById(pageAttachment.getAttachmentId())
                    .orElse(null);
            
            if (attachment != null) {
                PageResponse.AttachmentInfo attachmentInfo = PageResponse.AttachmentInfo.builder()
                        .id(attachment.getId())
                        .filename(attachment.getFilename())
                        .description(attachment.getDescription())
                        .build();
                attachmentInfos.add(attachmentInfo);
            }
        }
        
        // Build and return PageResponse
        return PageResponse.builder()
                .id(page.getId())
                .title(page.getTitle())
                .content(page.getContent())
                .spaceKey(page.getSpaceKey())
                .parentPageId(page.getParentPageId())
                .attachments(attachmentInfos)
                .build();
    }
}
