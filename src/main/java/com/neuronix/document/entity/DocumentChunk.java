package com.neuronix.document.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunks")
@Getter
@Setter
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(1536)")
    private float[] embedding;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}