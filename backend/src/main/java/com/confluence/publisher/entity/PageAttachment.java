package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "page_attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_id", nullable = false)
    private Long pageId;

    @Column(name = "attachment_id", nullable = false)
    private Long attachmentId;

    @Builder.Default
    @Column(nullable = false)
    private Integer position = 0;
}
