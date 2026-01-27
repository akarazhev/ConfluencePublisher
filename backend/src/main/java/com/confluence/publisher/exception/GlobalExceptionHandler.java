package com.confluence.publisher.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.warn("RuntimeException: {}", ex.getMessage(), ex);
        
        String message = ex.getMessage() != null ? ex.getMessage() : "Runtime error occurred";
        String lowerMessage = message.toLowerCase();
        
        // Check if message contains "not found" (case-insensitive)
        if (lowerMessage.contains("not found")) {
            Map<String, String> response = new HashMap<>();
            response.put("detail", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("detail", message);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        
        // Extract field errors from validation exception
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null 
                                ? error.getDefaultMessage() 
                                : "Validation failed",
                        (existing, replacement) -> existing // Handle duplicate keys
                ));
        
        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        
        // Don't expose internal details for generic exceptions
        Map<String, String> response = new HashMap<>();
        response.put("detail", "Internal server error");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
