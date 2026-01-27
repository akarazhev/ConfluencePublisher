package com.confluence.publisher.provider;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.provider.dto.ConfluencePageRequest;
import com.confluence.publisher.provider.dto.ConfluencePageResponse;
import com.confluence.publisher.provider.dto.ConfluenceSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class ConfluenceServerProvider implements BaseProvider {

    private final AppProperties appProperties;
    private volatile RestClient restClient;

    public ConfluenceServerProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

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

        // Authentication: Bearer token if API token > 30 chars, otherwise Basic auth
        String apiToken = appProperties.getConfluenceApiToken();
        String username = appProperties.getConfluenceUsername();

        if (StringUtils.hasText(apiToken) && apiToken.length() > 30) {
            // Bearer token authentication
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken);
            log.info("Using Bearer token authentication for Confluence API");
        } else if (StringUtils.hasText(username) && StringUtils.hasText(apiToken)) {
            // Basic authentication
            String credentials = username + ":" + apiToken;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
            log.info("Using Basic authentication for Confluence API");
        } else {
            log.warn("No authentication credentials configured for Confluence API");
        }

        return builder.build();
    }

    @Override
    public ProviderResult publishPage(String spaceKey, String title, String content, Long parentPageId, List<String> attachmentPaths) {
        log.info("Publishing page to Confluence server: space={}, title={}", spaceKey, title);

        // 1. Search for existing page by title in space
        ConfluencePageResponse existingPage = findPageByTitle(spaceKey, title);

        ConfluencePageResponse publishedPage;
        if (existingPage != null) {
            // 2. If exists → update page (increment version number)
            log.info("Page exists with ID: {}, updating...", existingPage.getId());
            publishedPage = updatePage(existingPage.getId(), spaceKey, title, content, parentPageId, existingPage.getVersion().getNumber());
        } else {
            // 3. If not exists → create new page
            log.info("Page does not exist, creating new page...");
            publishedPage = createPage(spaceKey, title, content, parentPageId);
        }

        // 4. Upload attachments if any
        if (attachmentPaths != null && !attachmentPaths.isEmpty()) {
            log.info("Uploading {} attachments to page ID: {}", attachmentPaths.size(), publishedPage.getId());
            uploadAttachments(publishedPage.getId(), attachmentPaths);
        }

        // 5. Return ProviderResult with page ID and web URL
        String webUrl = buildWebUrl(publishedPage);
        String message = String.format("Page published successfully. URL: %s", webUrl);
        return new ProviderResult(publishedPage.getId(), message);
    }

    @Override
    public String getStatus(String confluencePageId) {
        log.info("Getting status for Confluence page ID: {}", confluencePageId);
        try {
            ConfluencePageResponse page = getRestClient().get()
                    .uri("/rest/api/content/{id}", confluencePageId)
                    .retrieve()
                    .body(ConfluencePageResponse.class);

            return page != null ? page.getStatus() : "unknown";
        } catch (Exception e) {
            log.error("Error getting status for page ID: {}", confluencePageId, e);
            return "error";
        }
    }

    private ConfluencePageResponse findPageByTitle(String spaceKey, String title) {
        log.debug("Searching for page: space={}, title={}", spaceKey, title);
        try {
            ConfluenceSearchResponse searchResponse = getRestClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/api/content")
                            .queryParam("spaceKey", spaceKey)
                            .queryParam("title", title)
                            .queryParam("expand", "version")
                            .build())
                    .retrieve()
                    .body(ConfluenceSearchResponse.class);

            if (searchResponse != null && searchResponse.getResults() != null && !searchResponse.getResults().isEmpty()) {
                return searchResponse.getResults().get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("Error searching for page: space={}, title={}", spaceKey, title, e);
            return null;
        }
    }

    private ConfluencePageResponse createPage(String spaceKey, String title, String content, Long parentPageId) {
        log.debug("Creating new page: space={}, title={}", spaceKey, title);

        ConfluencePageRequest request = ConfluencePageRequest.builder()
                .type("page")
                .title(title)
                .space(ConfluencePageRequest.Space.builder().key(spaceKey).build())
                .body(ConfluencePageRequest.Body.builder()
                        .storage(ConfluencePageRequest.Body.Storage.builder()
                                .value(content != null ? content : "")
                                .representation("storage")
                                .build())
                        .build())
                .build();

        // Add parent page if specified
        if (parentPageId != null) {
            request.setAncestors(List.of(
                    ConfluencePageRequest.Ancestor.builder()
                            .id(String.valueOf(parentPageId))
                            .build()
            ));
        }

        return getRestClient().post()
                .uri("/rest/api/content")
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    private ConfluencePageResponse updatePage(String pageId, String spaceKey, String title, String content, Long parentPageId, Integer currentVersion) {
        log.debug("Updating page ID: {}, version: {}", pageId, currentVersion);

        ConfluencePageRequest request = ConfluencePageRequest.builder()
                .type("page")
                .title(title)
                .space(ConfluencePageRequest.Space.builder().key(spaceKey).build())
                .body(ConfluencePageRequest.Body.builder()
                        .storage(ConfluencePageRequest.Body.Storage.builder()
                                .value(content != null ? content : "")
                                .representation("storage")
                                .build())
                        .build())
                .version(ConfluencePageRequest.Version.builder()
                        .number(currentVersion + 1)
                        .build())
                .build();

        // Add parent page if specified
        if (parentPageId != null) {
            request.setAncestors(List.of(
                    ConfluencePageRequest.Ancestor.builder()
                            .id(String.valueOf(parentPageId))
                            .build()
            ));
        }

        return getRestClient().put()
                .uri("/rest/api/content/{id}", pageId)
                .body(request)
                .retrieve()
                .body(ConfluencePageResponse.class);
    }

    private void uploadAttachments(String pageId, List<String> attachmentPaths) {
        log.debug("Uploading attachments to page ID: {}", pageId);

        for (String attachmentPath : attachmentPaths) {
            try {
                Path path = Paths.get(attachmentPath);
                if (!path.toFile().exists()) {
                    log.warn("Attachment file does not exist: {}", attachmentPath);
                    continue;
                }

                FileSystemResource fileResource = new FileSystemResource(path);
                String filename = path.getFileName().toString();

                MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
                bodyBuilder.part("file", fileResource)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"file\"; filename=\"" + filename + "\"");

                getRestClient().post()
                        .uri("/rest/api/content/{id}/child/attachment", pageId)
                        .header("X-Atlassian-Token", "nocheck")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(bodyBuilder.build())
                        .retrieve()
                        .toBodilessEntity();

                log.info("Successfully uploaded attachment: {}", filename);
            } catch (Exception e) {
                log.error("Error uploading attachment: {}", attachmentPath, e);
            }
        }
    }

    private String buildWebUrl(ConfluencePageResponse page) {
        if (page != null && page.get_links() != null && page.get_links().getWebui() != null) {
            String baseUrl = appProperties.getConfluenceUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + page.get_links().getWebui();
        }
        return null;
    }
}
