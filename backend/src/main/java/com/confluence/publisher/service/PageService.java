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

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageService {

    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public Page createPage(String title, String content, String spaceKey, Long parentPageId, List<Long> attachmentIds) {
        log.info("Creating page with title: {}", title);
        
        Page page = Page.builder()
                .title(title)
                .content(content)
                .spaceKey(spaceKey)
                .parentPageId(parentPageId)
                .build();
        
        Page savedPage = pageRepository.save(page);
        log.debug("Page saved with ID: {}", savedPage.getId());
        
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            List<PageAttachment> pageAttachments = IntStream.range(0, attachmentIds.size())
                    .mapToObj(index -> PageAttachment.builder()
                            .pageId(savedPage.getId())
                            .attachmentId(attachmentIds.get(index))
                            .position(index)
                            .build())
                    .toList();
            
            pageAttachmentRepository.saveAll(pageAttachments);
            log.debug("Created {} page-attachment associations", pageAttachments.size());
        }
        
        return savedPage;
    }

    @Transactional(readOnly = true)
    public PageResponse getPage(Long pageId) {
        log.info("Fetching page with ID: {}", pageId);
        
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
        List<PageAttachment> pageAttachments = pageAttachmentRepository.findByPageIdOrderByPositionAsc(pageId);
        
        List<PageResponse.AttachmentInfo> attachmentInfos = pageAttachments.stream()
                .map(pa -> {
                    Attachment attachment = attachmentRepository.findById(pa.getAttachmentId())
                            .orElse(null);
                    if (attachment == null) {
                        log.warn("Attachment with ID {} not found for page {}", pa.getAttachmentId(), pageId);
                        return null;
                    }
                    return PageResponse.AttachmentInfo.builder()
                            .id(attachment.getId())
                            .filename(attachment.getFilename())
                            .description(attachment.getDescription())
                            .build();
                })
                .filter(info -> info != null)
                .toList();
        
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

