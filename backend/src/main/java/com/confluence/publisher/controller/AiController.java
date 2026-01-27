package com.confluence.publisher.controller;

import com.confluence.publisher.dto.AttachmentDescriptionRequest;
import com.confluence.publisher.dto.AttachmentDescriptionResponse;
import com.confluence.publisher.dto.ContentImprovementRequest;
import com.confluence.publisher.dto.ContentImprovementResponse;
import com.confluence.publisher.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/improve-content")
    public ResponseEntity<ContentImprovementResponse> improveContent(
            @Valid @RequestBody ContentImprovementRequest request) {
        // Stub implementation: Return variations of input (original, truncated, uppercase)
        List<String> suggestions = aiService.improveContent(request.getContent());
        
        ContentImprovementResponse response = ContentImprovementResponse.builder()
                .suggestions(suggestions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-description")
    public ResponseEntity<AttachmentDescriptionResponse> generateDescription(
            @Valid @RequestBody AttachmentDescriptionRequest request) {
        // Stub implementation: Return sanitized/truncated description or default
        String description = aiService.generateDescription(request.getDescription());
        
        AttachmentDescriptionResponse response = AttachmentDescriptionResponse.builder()
                .description(description)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
