package com.confluence.publisher.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Stub implementation for AI features.
 * This is a placeholder for future AI integration.
 */
@Slf4j
@Service
public class AiService {

    /**
     * Returns stub suggestions for content improvement.
     * This is a placeholder for future AI integration.
     *
     * @param content the content to improve
     * @return list of stub suggestions
     */
    public List<String> improveContent(String content) {
        List<String> suggestions = new ArrayList<>();
        
        // Original content
        suggestions.add(content);
        
        // Truncated version (first 100 chars + "...")
        if (content != null && content.length() > 100) {
            suggestions.add(content.substring(0, 100) + "...");
        } else if (content != null) {
            suggestions.add(content);
        }
        
        // Uppercase version
        if (content != null) {
            suggestions.add(content.toUpperCase());
        }
        
        log.debug("Generated {} stub suggestions for content improvement", suggestions.size());
        return suggestions;
    }

    /**
     * Generates a description from the input.
     * This is a placeholder for future AI integration.
     *
     * @param description the input description
     * @return sanitized/truncated description
     */
    public String generateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "No description provided";
        }
        
        // Sanitize and truncate to max 200 chars
        String sanitized = description.trim();
        if (sanitized.length() > 200) {
            sanitized = sanitized.substring(0, 200);
        }
        
        log.debug("Generated description (truncated to {} chars)", sanitized.length());
        return sanitized;
    }
}
