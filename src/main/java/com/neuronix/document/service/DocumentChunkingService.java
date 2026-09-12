package com.neuronix.document.service;

import com.neuronix.document.entity.Document;
import com.neuronix.document.entity.DocumentChunk;
import com.neuronix.document.repository.DocumentChunkRepository;
import com.neuronix.rag.embedding.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentChunkingService {

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;

    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;

    public List<DocumentChunk> chunkDocument(
            Document document,
            String text
    ) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Document text must not be empty"
            );
        }

        List<DocumentChunk> chunks = new ArrayList<>();

        int start = 0;
        int chunkIndex = 0;

        while (start < text.length()) {

            int end = Math.min(
                    start + CHUNK_SIZE,
                    text.length()
            );

            String chunkText = text
                    .substring(start, end)
                    .trim();

            if (!chunkText.isEmpty()) {

                DocumentChunk chunk = new DocumentChunk();

                chunk.setDocument(document);
                chunk.setChunkIndex(chunkIndex++);
                chunk.setContent(chunkText);
                chunk.setCreatedAt(LocalDateTime.now());

                /*
                 * Generate embedding for this chunk.
                 */
                float[] embedding =
                        embeddingService.generateEmbedding(chunkText);

                chunk.setEmbedding(embedding);

                chunks.add(chunk);
            }

            if (end == text.length()) {
                break;
            }

            start = end - CHUNK_OVERLAP;
        }

        return documentChunkRepository.saveAll(chunks);
    }
}