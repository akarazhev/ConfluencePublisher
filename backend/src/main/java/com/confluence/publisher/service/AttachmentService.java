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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {
    
    private final AttachmentRepository attachmentRepository;
    private final AppProperties appProperties;
    
    @Transactional
    public Attachment uploadAttachment(MultipartFile file, String description) throws IOException {
        log.info("Uploading attachment: {}, size: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // Create attachment directory if needed
        Path attachmentDir = Paths.get(appProperties.getAttachmentDir());
        Files.createDirectories(attachmentDir);
        log.debug("Attachment directory ensured: {}", attachmentDir.toAbsolutePath());
        
        // Generate UUID-based filename preserving original extension
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uuidFilename = UUID.randomUUID().toString() + extension;
        Path filePath = attachmentDir.resolve(uuidFilename);
        
        // Write file bytes to disk
        Files.write(filePath, file.getBytes());
        log.debug("File written to: {}", filePath.toAbsolutePath());
        
        // Create Attachment entity with original filename, content type, size, storage path
        Attachment attachment = Attachment.builder()
                .filename(originalFilename != null ? originalFilename : uuidFilename)
                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .size(file.getSize())
                .storagePath(filePath.toString())
                .description(description)
                .build();
        
        // Save and return the attachment
        Attachment savedAttachment = attachmentRepository.save(attachment);
        log.info("Attachment saved with ID: {}", savedAttachment.getId());
        
        return savedAttachment;
    }
}
