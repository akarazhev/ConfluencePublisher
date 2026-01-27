package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "publishlog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_id", nullable = false)
    private Long pageId;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "space_key", length = 50)
    private String spaceKey;

    @Column(name = "confluence_page_id", length = 100)
    private String confluencePageId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
