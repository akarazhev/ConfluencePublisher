package com.confluence.publisher.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfluenceSearchResponse {
    private List<ConfluencePageResponse> results;
    private Integer start;
    private Integer limit;
    private Integer size;
}
