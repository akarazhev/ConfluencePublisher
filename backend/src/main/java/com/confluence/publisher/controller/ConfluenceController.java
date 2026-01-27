package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ConfluencePublishRequest;
import com.confluence.publisher.dto.PublishResponse;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.service.PublishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/confluence")
@RequiredArgsConstructor
public class ConfluenceController {

    private final PublishService publishService;

    @PostMapping("/publish")
    public ResponseEntity<PublishResponse> publish(@Valid @RequestBody ConfluencePublishRequest request) {
        try {
            // Call PublishService.publishPage() and return result
            PublishLog publishLog = publishService.publishPage(request.getPageId());
            
            PublishResponse response = PublishResponse.builder()
                    .logId(publishLog.getId())
                    .status(publishLog.getStatus())
                    .confluencePageId(publishLog.getConfluencePageId())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            log.error("Error publishing page", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
