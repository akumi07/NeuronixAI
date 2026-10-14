package com.neuronix.rag.retrieval.service;

import com.neuronix.rag.embedding.EmbeddingService;
import com.neuronix.rag.retrieval.dto.RetrievalResult;
import com.neuronix.rag.retrieval.repository.DocumentRetrievalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentRetrievalService {

    private final EmbeddingService embeddingService;
    private final DocumentRetrievalRepository documentRetrievalRepository;

    public List<RetrievalResult> retrieveRelevantChunks(
            Long documentId,
            String question,
            int topK
    ) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "Question must not be empty"
            );
        }

        if (topK <= 0) {
            throw new IllegalArgumentException(
                    "topK must be greater than zero"
            );
        }

        float[] queryEmbedding =
                embeddingService.generateEmbedding(question);

        return documentRetrievalRepository.findSimilarChunks(
                documentId,
                queryEmbedding,
                topK
        );
    }
}