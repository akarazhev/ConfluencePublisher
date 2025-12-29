package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleCreateRequest {
    
    @NotNull
    private Long pageId;
    
    private Instant scheduledAt;
}

