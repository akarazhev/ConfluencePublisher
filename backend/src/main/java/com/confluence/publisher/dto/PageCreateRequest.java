package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageCreateRequest {
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String content;
    
    private String spaceKey;
    
    private Long parentPageId;
    
    @NotNull
    private List<Long> attachmentIds = new ArrayList<>();
}

