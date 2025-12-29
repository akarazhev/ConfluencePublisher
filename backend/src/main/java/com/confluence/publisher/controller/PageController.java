package com.confluence.publisher.controller;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.dto.PageCreateRequest;
import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.service.PageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;
    private final AppProperties appProperties;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PageResponse createPage(@Valid @RequestBody PageCreateRequest request) {
        // Use default space from AppProperties if spaceKey not provided
        String spaceKey = request.getSpaceKey() != null && !request.getSpaceKey().isBlank()
                ? request.getSpaceKey()
                : appProperties.getConfluenceDefaultSpace();

        // Call PageService.createPage()
        Page page = pageService.createPage(
                request.getTitle(),
                request.getContent(),
                spaceKey,
                request.getParentPageId(),
                request.getAttachmentIds()
        );

        // Return created page info
        return pageService.getPage(page.getId());
    }

    @GetMapping("/{pageId}")
    public PageResponse getPage(@PathVariable Long pageId) {
        return pageService.getPage(pageId);
    }
}

