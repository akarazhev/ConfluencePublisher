package com.confluence.publisher.controller;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.dto.PageCreateRequest;
import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.service.PageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;
    private final AppProperties appProperties;

    @PostMapping
    public ResponseEntity<PageResponse> createPage(@Valid @RequestBody PageCreateRequest request) {
        // Use default space from AppProperties if spaceKey not provided
        String spaceKey = StringUtils.hasText(request.getSpaceKey())
                ? request.getSpaceKey()
                : appProperties.getConfluenceDefaultSpace();

        // Call PageService.createPage()
        var page = pageService.createPage(
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
        try {
            PageResponse response = pageService.getPage(pageId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}
