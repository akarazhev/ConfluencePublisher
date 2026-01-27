package com.confluence.publisher.controller;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.dto.ConfigResponse;
import com.confluence.publisher.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final AppProperties appProperties;

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(HealthResponse.builder()
                .status("ok")
                .build());
    }

    @GetMapping("/config")
    public ResponseEntity<ConfigResponse> config() {
        return ResponseEntity.ok(ConfigResponse.builder()
                .defaultSpace(appProperties.getConfluenceDefaultSpace())
                .build());
    }
}
