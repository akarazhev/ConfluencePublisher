package com.confluence.publisher.controller;

import com.confluence.publisher.dto.AttachmentUploadResponse;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentUploadResponse uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        
        Attachment attachment = attachmentService.uploadAttachment(file, description);

        return AttachmentUploadResponse.builder()
                .id(attachment.getId())
                .filename(attachment.getFilename())
                .description(attachment.getDescription())
                .build();
    }
}

