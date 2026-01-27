package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ConfluencePublishRequest;
import com.confluence.publisher.dto.PublishResponse;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.service.PublishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/confluence")
@RequiredArgsConstructor
public class ConfluenceController {

    private final PublishService publishService;

    @PostMapping("/publish")
    public ResponseEntity<PublishResponse> publish(@Valid @RequestBody ConfluencePublishRequest request) {
        // Call PublishService.publishPage() and return result
        PublishLog publishLog = publishService.publishPage(request.getPageId());
        
        PublishResponse response = PublishResponse.builder()
                .logId(publishLog.getId())
                .status(publishLog.getStatus())
                .confluencePageId(publishLog.getConfluencePageId())
                .build();
        
        return ResponseEntity.ok(response);
    }
}
