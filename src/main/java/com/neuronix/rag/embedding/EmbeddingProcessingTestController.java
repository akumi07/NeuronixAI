package com.neuronix.rag.embedding;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag/test")
@RequiredArgsConstructor
public class EmbeddingProcessingTestController {

    private final DocumentEmbeddingService documentEmbeddingService;

    @PostMapping("/documents/{documentId}/embeddings")
    public ResponseEntity<String> generateEmbeddings(
            @PathVariable Long documentId
    ) {

        int count =
                documentEmbeddingService.generateEmbeddings(
                        documentId
                );

        return ResponseEntity.ok(
                "Generated embeddings for "
                        + count
                        + " chunks"
        );
    }
}