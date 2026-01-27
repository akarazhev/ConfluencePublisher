package com.confluence.publisher.controller;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.dto.PageCreateRequest;
import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.service.PageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;
    private final AppProperties appProperties;

    @PostMapping
    public ResponseEntity<PageResponse> createPage(@Valid @RequestBody PageCreateRequest request) {
        // Use default space from AppProperties if spaceKey not provided
        String spaceKey = request.getSpaceKey();
        if (spaceKey == null || spaceKey.trim().isEmpty()) {
            spaceKey = appProperties.getConfluenceDefaultSpace();
        }

        // Call PageService.createPage()
        Page page = pageService.createPage(
                request.getTitle(),
                request.getContent(),
                spaceKey,
                request.getParentPageId(),
                request.getAttachmentIds()
        );

        // Return created page info
        PageResponse response = pageService.getPage(page.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{pageId}")
    public ResponseEntity<PageResponse> getPage(@PathVariable Long pageId) {
        PageResponse response = pageService.getPage(pageId);
        return ResponseEntity.ok(response);
    }
}
