package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.provider.dto.ConfluencePageRequest;
import com.confluence.publisher.provider.dto.ConfluencePageResponse;
import com.confluence.publisher.provider.dto.ConfluenceSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 * Real Confluence Server integration using Spring's RestClient.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConfluenceServerProvider implements BaseProvider {

    private final AppProperties appProperties;
    private volatile RestClient restClient;

    /**
     * Lazy initialization of RestClient with double-checked locking.
     */
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

    /**
     * Builds RestClient with proper authentication and headers.
     */
    private RestClient buildRestClient() {
        String baseUrl = appProperties.getConfluenceUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String authHeader = buildAuthHeader();

        log.info("Initializing Confluence REST client with base URL: {}", baseUrl);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", authHeader)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Builds authorization header based on API token length.
     * Bearer token if API token > 30 chars, otherwise Basic auth.
     */
    private String buildAuthHeader() {
        String apiToken = appProperties.getConfluenceApiToken();
        
        if (apiToken != null && apiToken.length() > 30) {
            // Use Bearer token for API tokens
            return "Bearer " + apiToken;
        } else {
            // Use Basic auth
            String username = appProperties.getConfluenceUsername();
            String password = apiToken != null ? apiToken : "";
            String credentials = username + ":" + password;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            return "Basic " + encodedCredentials;
        }
    }

    @Override
    public ProviderResult publishPage(String spaceKey, String title, String content, String parentPageId, List<String> attachmentPaths) {
        try {
            // Search for existing page
            ConfluencePageResponse existingPage = findPageByTitle(spaceKey, title);

            ConfluencePageResponse resultPage;
            if (existingPage != null) {
                // Update existing page
                log.info("Updating existing page: {} (ID: {})", title, existingPage.getId());
                resultPage = updatePage(existingPage.getId(), title, content, spaceKey, parentPageId, existingPage.getVersion().getNumber());
            } else {
                // Create new page
                log.info("Creating new page: {}", title);
                resultPage = createPage(title, content, spaceKey, parentPageId);
            }

            // Upload attachments if any
            if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
                uploadAttachments(resultPage.getId(), attachmentPaths);
            }

            String webUrl = buildWebUrl(resultPage);
            return new ProviderResult(resultPage.getId(), "Successfully published to Confluence: " + webUrl);

        } catch (Exception e) {
            log.error("Failed to publish page to Confluence: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish page to Confluence: " + e.getMessage(), e);
        }
    }

    @Override
    public String getStatus(String confluencePageId) {
        try {
            ConfluencePageResponse page = getRestClient()
                    .get()
                    .uri("/rest/api/content/{id}", confluencePageId)
                    .retrieve()
                    .body(ConfluencePageResponse.class);
            
            return page != null ? page.getStatus() : "unknown";
        } catch (Exception e) {
            log.error("Failed to get status for page {}: {}", confluencePageId, e.getMessage());
            return "error";
        }
    }

    /**
     * Searches for a page by title in the specified space.
     */
    private ConfluencePageResponse findPageByTitle(String spaceKey, String title) {
        try {
            ConfluenceSearchResponse response = getRestClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/content")
                            .queryParam("spaceKey", spaceKey)
                            .queryParam("title", title)
                            .queryParam("expand", "version")
                            .build())
                    .retrieve()
                    .body(ConfluenceSearchResponse.class);

            if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
                return response.getResults().get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to search for page '{}' in space '{}': {}", title, spaceKey, e.getMessage());
            return null;
        }
    }

    /**
     * Creates a new page in Confluence.
     */
    private ConfluencePageResponse createPage(String title, String content, String spaceKey, String parentPageId) {
        ConfluencePageRequest request = new ConfluencePageRequest();
        request.setType("page");
        request.setTitle(title);
        request.setSpace(new ConfluencePageRequest.Space(spaceKey));

        ConfluencePageRequest.Storage storage = new ConfluencePageRequest.Storage();
        storage.setValue(content);
        storage.setRepresentation("storage");
        request.setBody(new ConfluencePageRequest.Body(storage));

        // Set parent page if provided
        if (parentPageId != null && !parentPageId.isEmpty()) {
            request.setAncestors(List.of(new ConfluencePageRequest.Ancestor(parentPageId)));
        }

        return getRestClient()
                .post()
                .uri("/rest/api/content")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    /**
     * Updates an existing page in Confluence.
     */
    private ConfluencePageResponse updatePage(String pageId, String title, String content, String spaceKey, String parentPageId, Integer currentVersion) {
        ConfluencePageRequest request = new ConfluencePageRequest();
        request.setType("page");
        request.setTitle(title);
        request.setSpace(new ConfluencePageRequest.Space(spaceKey));

        ConfluencePageRequest.Storage storage = new ConfluencePageRequest.Storage();
        storage.setValue(content);
        storage.setRepresentation("storage");
        request.setBody(new ConfluencePageRequest.Body(storage));

        // Increment version number
        request.setVersion(new ConfluencePageRequest.Version(currentVersion + 1));

        // Set parent page if provided
        if (parentPageId != null && !parentPageId.isEmpty()) {
            request.setAncestors(List.of(new ConfluencePageRequest.Ancestor(parentPageId)));
        }

        return getRestClient()
                .put()
                .uri("/rest/api/content/{id}", pageId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    /**
     * Uploads attachments to a Confluence page.
     */
    private void uploadAttachments(String pageId, List<String> attachmentPaths) {
        for (String attachmentPath : attachmentPaths) {
            try {
                uploadAttachment(pageId, attachmentPath);
            } catch (Exception e) {
                log.error("Failed to upload attachment '{}': {}", attachmentPath, e.getMessage(), e);
            }
        }
    }

    /**
     * Uploads a single attachment to a Confluence page.
     */
    private void uploadAttachment(String pageId, String attachmentPath) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(Path.of(attachmentPath)));

        getRestClient()
                .post()
                .uri("/rest/api/content/{id}/child/attachment", pageId)
                .header("X-Atlassian-Token", "nocheck")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .toBodilessEntity();

        log.info("Successfully uploaded attachment: {}", attachmentPath);
    }

    /**
     * Builds the web URL for a Confluence page.
     */
    private String buildWebUrl(ConfluencePageResponse page) {
        if (page.getLinks() != null && page.getLinks().getWebui() != null) {
            String baseUrl = appProperties.getConfluenceUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + page.getLinks().getWebui();
        }
        return "URL not available";
    }
}

