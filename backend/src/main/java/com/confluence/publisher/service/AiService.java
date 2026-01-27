package com.confluence.publisher.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AI service for content improvement and description generation.
 * This is a stub implementation that will be replaced with real AI integration in the future.
 */
@Slf4j
@Service
public class AiService {
    
    /**
     * Improves content by returning stub suggestions.
     * This is a placeholder for future AI integration.
     * 
     * @param content The original content
     * @return List of suggestions: original content, truncated version, uppercase version
     */
    public List<String> improveContent(String content) {
        log.debug("Improving content (stub implementation)");
        
        List<String> suggestions = new ArrayList<>();
        
        // Original content
        suggestions.add(content);
        
        // Truncated version (first 100 chars + "...")
        if (content != null && content.length() > 100) {
            suggestions.add(content.substring(0, 100) + "...");
        } else {
            suggestions.add(content != null ? content : "");
        }
        
        // Uppercase version
        suggestions.add(content != null ? content.toUpperCase() : "");
        
        return suggestions;
    }
    
    /**
     * Generates or sanitizes a description.
     * This is a placeholder for future AI integration.
     * 
     * @param description The input description
     * @return Sanitized/truncated description (max 200 chars) or "No description provided"
     */
    public String generateDescription(String description) {
        log.debug("Generating description (stub implementation)");
        
        // If description is null or blank, return "No description provided"
        if (description == null || description.trim().isEmpty()) {
            return "No description provided";
        }
        
        // Otherwise return sanitized/truncated description (max 200 chars)
        String sanitized = description.trim();
        if (sanitized.length() > 200) {
            return sanitized.substring(0, 200);
        }
        
        return sanitized;
    }
}
