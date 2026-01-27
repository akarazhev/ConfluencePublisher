package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.provider.dto.ConfluencePageRequest;
import com.confluence.publisher.provider.dto.ConfluencePageResponse;
import com.confluence.publisher.provider.dto.ConfluenceSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfluenceServerProvider implements BaseProvider {

    private final AppProperties appProperties;
    private volatile RestClient restClient;

    private RestClient getRestClient() {
        if (restClient == null) {
            synchronized (this) {
                if (restClient == null) {
                    restClient = buildRestClient();
                }
            }
        }
        return restClient;
    }

    private RestClient buildRestClient() {
        String baseUrl = appProperties.getConfluenceUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        // Authentication
        String apiToken = appProperties.getConfluenceApiToken();
        String username = appProperties.getConfluenceUsername();
        
        if (apiToken != null && apiToken.length() > 30) {
            // Bearer token authentication
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken);
            log.debug("Using Bearer token authentication");
        } else if (username != null && !username.isEmpty() && apiToken != null && !apiToken.isEmpty()) {
            // Basic authentication
            String credentials = username + ":" + apiToken;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
            log.debug("Using Basic authentication for user: {}", username);
        } else {
            log.warn("No valid authentication credentials found. API calls may fail.");
        }

        return builder.build();
    }

    @Override
    public ProviderResult publishPage(String spaceKey, String title, String content,
                                      Long parentPageId, List<String> attachmentPaths) {
        try {
            log.info("Publishing page '{}' to space '{}'", title, spaceKey);
            
            // Search for existing page
            ConfluencePageResponse existingPage = findPageByTitle(spaceKey, title);
            
            ConfluencePageResponse pageResponse;
            if (existingPage != null) {
                log.info("Page '{}' already exists (ID: {}), updating...", title, existingPage.getId());
                pageResponse = updatePage(existingPage.getId(), title, content, 
                        existingPage.getVersion().getNumber(), parentPageId);
            } else {
                log.info("Page '{}' does not exist, creating new page...", title);
                pageResponse = createPage(spaceKey, title, content, parentPageId);
            }
            
            // Upload attachments if any
            if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
                log.info("Uploading {} attachment(s) to page {}", attachmentPaths.size(), pageResponse.getId());
                uploadAttachments(pageResponse.getId(), attachmentPaths);
            }
            
            String webUrl = buildWebUrl(pageResponse);
            String message = String.format("Page '%s' published successfully. URL: %s", title, webUrl);
            
            return new ProviderResult(pageResponse.getId(), message);
            
        } catch (RestClientException e) {
            log.error("Error publishing page '{}' to Confluence", title, e);
            throw new RuntimeException("Failed to publish page to Confluence: " + e.getMessage(), e);
        }
    }

    @Override
    public String getStatus(String confluencePageId) {
        try {
            log.debug("Getting status for Confluence page ID: {}", confluencePageId);
            ConfluencePageResponse page = getRestClient().get()
                    .uri("/rest/api/content/{id}", confluencePageId)
                    .retrieve()
                    .body(ConfluencePageResponse.class);
            
            return page != null ? page.getStatus() : "unknown";
        } catch (RestClientException e) {
            log.error("Error getting status for page ID: {}", confluencePageId, e);
            return "error";
        }
    }

    private ConfluencePageResponse findPageByTitle(String spaceKey, String title) {
        try {
            log.debug("Searching for page with title '{}' in space '{}'", title, spaceKey);
            ConfluenceSearchResponse searchResponse = getRestClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/content")
                            .queryParam("spaceKey", spaceKey)
                            .queryParam("title", title)
                            .queryParam("expand", "version")
                            .build())
                    .retrieve()
                    .body(ConfluenceSearchResponse.class);
            
            if (searchResponse != null && searchResponse.getResults() != null 
                    && !searchResponse.getResults().isEmpty()) {
                return searchResponse.getResults().get(0);
            }
            return null;
        } catch (RestClientException e) {
            log.warn("Error searching for page '{}' in space '{}': {}", title, spaceKey, e.getMessage());
            return null;
        }
    }

    private ConfluencePageResponse createPage(String spaceKey, String title, String content,
                                             Long parentPageId) {
        ConfluencePageRequest request = ConfluencePageRequest.builder()
                .type("page")
                .title(title)
                .space(ConfluencePageRequest.Space.builder().key(spaceKey).build())
                .body(ConfluencePageRequest.Body.builder()
                        .storage(ConfluencePageRequest.Body.Storage.builder()
                                .value(content)
                                .representation("storage")
                                .build())
                        .build())
                .ancestors(parentPageId != null ? 
                        List.of(ConfluencePageRequest.Ancestor.builder().id(parentPageId).build()) : 
                        null)
                .build();

        return getRestClient().post()
                .uri("/rest/api/content")
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    private ConfluencePageResponse updatePage(String pageId, String title, String content,
                                             Integer currentVersion, Long parentPageId) {
        ConfluencePageRequest request = ConfluencePageRequest.builder()
                .type("page")
                .title(title)
                .body(ConfluencePageRequest.Body.builder()
                        .storage(ConfluencePageRequest.Body.Storage.builder()
                                .value(content)
                                .representation("storage")
                                .build())
                        .build())
                .ancestors(parentPageId != null ? 
                        List.of(ConfluencePageRequest.Ancestor.builder().id(parentPageId).build()) : 
                        null)
                .version(ConfluencePageRequest.Version.builder()
                        .number(currentVersion + 1)
                        .build())
                .build();

        return getRestClient().put()
                .uri("/rest/api/content/{id}", pageId)
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    private void uploadAttachments(String pageId, List<String> attachmentPaths) {
        for (String attachmentPath : attachmentPaths) {
            try {
                Path path = Paths.get(attachmentPath);
                if (!path.toFile().exists()) {
                    log.warn("Attachment file does not exist: {}", attachmentPath);
                    continue;
                }

                String filename = path.getFileName().toString();
                FileSystemResource resource = new FileSystemResource(path.toFile());

                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", resource);
                body.add("comment", "Uploaded via Confluence Publisher");

                log.debug("Uploading attachment '{}' to page {}", filename, pageId);
                
                getRestClient().post()
                        .uri("/rest/api/content/{id}/child/attachment", pageId)
                        .header("X-Atlassian-Token", "nocheck")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(body)
                        .retrieve()
                        .toBodilessEntity();

                log.info("Successfully uploaded attachment '{}' to page {}", filename, pageId);
            } catch (RestClientException e) {
                log.error("Error uploading attachment '{}' to page {}: {}", 
                        attachmentPath, pageId, e.getMessage(), e);
                // Continue with other attachments even if one fails
            }
        }
    }

    private String buildWebUrl(ConfluencePageResponse page) {
        if (page != null && page.get_links() != null && page.get_links().getWebui() != null) {
            String webui = page.get_links().getWebui();
            if (webui.startsWith("/")) {
                // Relative URL, prepend base URL
                String baseUrl = appProperties.getConfluenceUrl();
                if (baseUrl.endsWith("/")) {
                    baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
                }
                return baseUrl + webui;
            }
            return webui;
        }
        // Fallback: construct URL from page ID
        String baseUrl = appProperties.getConfluenceUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + "/pages/viewpage.action?pageId=" + page.getId();
    }
}
