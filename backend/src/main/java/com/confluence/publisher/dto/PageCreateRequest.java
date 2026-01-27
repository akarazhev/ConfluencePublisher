package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String spaceKey;

    private Long parentPageId;

    @NotNull(message = "Attachment IDs cannot be null")
    private List<Long> attachmentIds = new ArrayList<>();
}
