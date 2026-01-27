package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_id", nullable = false)
    private Long pageId;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String status = "queued";

    @Builder.Default
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;
}
