package com.confluence.publisher.controller;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.dto.ConfigResponse;
import com.confluence.publisher.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final AppProperties appProperties;

    @GetMapping("/health")
    public HealthResponse health() {
        return HealthResponse.builder()
                .status("ok")
                .build();
    }

    @GetMapping("/config")
    public ConfigResponse config() {
        return ConfigResponse.builder()
                .defaultSpace(appProperties.getConfluenceDefaultSpace())
                .build();
    }
}

