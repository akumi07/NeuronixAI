package com.neuronix.document.repository;

import com.neuronix.document.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk> findByDocumentIdOrderByChunkIndex(
            Long documentId
    );

    List<DocumentChunk> findByDocumentIdAndEmbeddingIsNullOrderByChunkIndex(
            Long documentId
    );
}