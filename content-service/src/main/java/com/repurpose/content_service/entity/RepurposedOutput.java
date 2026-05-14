package com.repurpose.content_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho 1 output đã tái chế (tương đương Transaction trong Fintech)
 * Mỗi RepurposedOutput thuộc về 1 ContentProject
 */
@Entity
@Table(name = "repurposed_outputs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepurposedOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private ContentProject project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OutputFormat outputFormat;

    @Column(columnDefinition = "TEXT")
    private String generatedContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OutputStatus status = OutputStatus.PENDING;

    private Integer tokensUsed;

    @Column(length = 512)
    private String imageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
