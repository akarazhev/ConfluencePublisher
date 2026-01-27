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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageService {

    private final PageRepository pageRepository;
    private final PageAttachmentRepository pageAttachmentRepository;
    private final AttachmentRepository attachmentRepository;

    @Transactional
    public Page createPage(String title, String content, String spaceKey, Long parentPageId, List<Long> attachmentIds) {
        Page page = Page.builder()
                .title(title)
                .content(content)
                .spaceKey(spaceKey)
                .parentPageId(parentPageId)
                .build();
        
        Page savedPage = pageRepository.save(page);
        
        if (attachmentIds != null && !attachmentIds.isEmpty()) {
            for (int i = 0; i < attachmentIds.size(); i++) {
                PageAttachment pageAttachment = PageAttachment.builder()
                        .pageId(savedPage.getId())
                        .attachmentId(attachmentIds.get(i))
                        .position(i)
                        .build();
                pageAttachmentRepository.save(pageAttachment);
            }
        }
        
        log.info("Created page with ID: {}", savedPage.getId());
        return savedPage;
    }

    @Transactional(readOnly = true)
    public PageResponse getPage(Long pageId) {
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageId));
        
        List<PageAttachment> pageAttachments = pageAttachmentRepository.findByPageIdOrderByPosition(pageId);
        
        List<PageResponse.AttachmentInfo> attachmentInfos = pageAttachments.stream()
                .map(pa -> {
                    Attachment attachment = attachmentRepository.findById(pa.getAttachmentId())
                            .orElse(null);
                    if (attachment != null) {
                        return PageResponse.AttachmentInfo.builder()
                                .id(attachment.getId())
                                .filename(attachment.getFilename())
                                .description(attachment.getDescription())
                                .build();
                    }
                    return null;
                })
                .filter(info -> info != null)
                .collect(Collectors.toList());
        
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
