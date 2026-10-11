package com.neuronix.rag.embedding;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/rag/test")
@RequiredArgsConstructor
public class EmbeddingTestController {

    private final EmbeddingService embeddingService;

    @PostMapping("/embedding")
    public ResponseEntity<String> generateEmbedding(
            @RequestBody String text
    ) {

        float[] embedding =
                embeddingService.generateEmbedding(text);

        return ResponseEntity.ok(
                "Embedding dimensions: " + embedding.length
                        + "\nFirst 10 values: "
                        + Arrays.toString(
                        Arrays.copyOf(
                                embedding,
                                Math.min(10, embedding.length)
                        )
                )
        );
    }
}