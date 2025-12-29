package com.confluence.publisher.service;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AppProperties appProperties;

    @Transactional
    public Attachment uploadAttachment(MultipartFile file, String description) {
        log.info("Uploading attachment: {}", file.getOriginalFilename());
        
        try {
            // Create attachment directory if needed
            Path attachmentDir = Paths.get(appProperties.getAttachmentDir());
            if (!Files.exists(attachmentDir)) {
                Files.createDirectories(attachmentDir);
                log.debug("Created attachment directory: {}", attachmentDir);
            }
            
            // Generate UUID-based filename preserving original extension
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String storedFilename = UUID.randomUUID().toString() + extension;
            Path storagePath = attachmentDir.resolve(storedFilename);
            
            // Write file bytes to disk
            Files.write(storagePath, file.getBytes());
            log.debug("File saved to: {}", storagePath);
            
            // Create Attachment entity
            Attachment attachment = Attachment.builder()
                    .filename(originalFilename != null ? originalFilename : storedFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .storagePath(storagePath.toString())
                    .description(description)
                    .build();
            
            Attachment savedAttachment = attachmentRepository.save(attachment);
            log.info("Attachment saved with ID: {}", savedAttachment.getId());
            
            return savedAttachment;
            
        } catch (IOException e) {
            log.error("Failed to upload attachment: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload attachment: " + e.getMessage(), e);
        }
    }
}

