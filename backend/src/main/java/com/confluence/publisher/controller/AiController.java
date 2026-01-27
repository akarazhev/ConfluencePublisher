package com.confluence.publisher.controller;

import com.confluence.publisher.dto.AttachmentDescriptionRequest;
import com.confluence.publisher.dto.AttachmentDescriptionResponse;
import com.confluence.publisher.dto.ContentImprovementRequest;
import com.confluence.publisher.dto.ContentImprovementResponse;
import com.confluence.publisher.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @RequestBody AttachmentDescriptionRequest request) {
        // Stub implementation: Return sanitized/truncated description or default
        String description = aiService.generateDescription(request.getDescription());
        
        AttachmentDescriptionResponse response = AttachmentDescriptionResponse.builder()
                .description(description)
                .build();
        
        return ResponseEntity.ok(response);
    }
}
