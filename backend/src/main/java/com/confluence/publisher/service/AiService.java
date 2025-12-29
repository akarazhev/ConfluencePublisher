package com.confluence.publisher.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AiService {

    /**
     * Stub implementation for content improvement.
     * Returns basic variations of the content for testing purposes.
     * This is a placeholder for future AI integration.
     */
    public List<String> improveContent(String content) {
        log.info("Generating content improvements (stub implementation)");
        
        if (content == null || content.isBlank()) {
            return Arrays.asList("", "", "");
        }
        
        // Return stub suggestions: original content, truncated version, uppercase version
        String truncated = content.length() > 100 
                ? content.substring(0, 100) + "..." 
                : content;
        
        String uppercase = content.toUpperCase();
        
        return Arrays.asList(content, truncated, uppercase);
    }

    /**
     * Stub implementation for description generation.
     * Returns sanitized/truncated description.
     * This is a placeholder for future AI integration.
     */
    public String generateDescription(String description) {
        log.info("Generating description (stub implementation)");
        
        if (description == null || description.isBlank()) {
            return "No description provided";
        }
        
        // Sanitize and truncate to max 200 chars
        String sanitized = description.trim();
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }
        
        return sanitized;
    }
}

