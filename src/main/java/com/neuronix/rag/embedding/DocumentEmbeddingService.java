package com.neuronix.rag.embedding;

import com.neuronix.document.entity.DocumentChunk;
import com.neuronix.document.repository.DocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentEmbeddingService {

    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;

    @Transactional
    public int generateEmbeddings(Long documentId) {

        List<DocumentChunk> chunks =
                documentChunkRepository
                        .findByDocumentIdAndEmbeddingIsNullOrderByChunkIndex(
                                documentId
                        );

        for (DocumentChunk chunk : chunks) {

            float[] embedding =
                    embeddingService.generateEmbedding(
                            chunk.getContent()
                    );

            chunk.setEmbedding(embedding);
        }

        documentChunkRepository.saveAll(chunks);

        return chunks.size();
    }
}