package com.confluence.publisher.controller;

import com.confluence.publisher.dto.AttachmentDescriptionRequest;
import com.confluence.publisher.dto.AttachmentDescriptionResponse;
import com.confluence.publisher.dto.ContentImprovementRequest;
import com.confluence.publisher.dto.ContentImprovementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    @PostMapping("/improve-content")
    public ContentImprovementResponse improveContent(@Valid @RequestBody ContentImprovementRequest request) {
        // Stub implementation: return variations of input (original, truncated, uppercase)
        String content = request.getContent();
        
        List<String> suggestions = Arrays.asList(
                content,
                content.length() > 50 ? content.substring(0, 50) + "..." : content,
                content.toUpperCase()
        );

        return ContentImprovementResponse.builder()
                .suggestions(suggestions)
                .build();
    }

    @PostMapping("/generate-description")
    public AttachmentDescriptionResponse generateDescription(@Valid @RequestBody AttachmentDescriptionRequest request) {
        // Stub implementation: return sanitized/truncated description or default
        String description = request.getDescription();
        
        String result;
        if (description == null || description.isBlank()) {
            result = "Auto-generated description";
        } else {
            // Sanitize and truncate to 100 characters
            result = description.trim();
            if (result.length() > 100) {
                result = result.substring(0, 100) + "...";
            }
        }

        return AttachmentDescriptionResponse.builder()
                .description(result)
                .build();
    }
}

