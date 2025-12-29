package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Confluence search API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluenceSearchResponse {

    private List<ConfluencePageResponse> results;
    private Integer start;
    private Integer limit;
    private Integer size;
}

