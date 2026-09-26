package com.neuronix.rag.retrieval.controller;

import com.neuronix.rag.retrieval.dto.RetrievalResult;
import com.neuronix.rag.retrieval.service.DocumentRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag/test")
@RequiredArgsConstructor
public class RetrievalTestController {

    private final DocumentRetrievalService documentRetrievalService;

    @GetMapping("/documents/{documentId}/search")
    public ResponseEntity<List<RetrievalResult>> search(
            @PathVariable Long documentId,
            @RequestParam String question,
            @RequestParam(defaultValue = "5") int topK
    ) {

        List<RetrievalResult> results =
                documentRetrievalService.retrieveRelevantChunks(
                        documentId,
                        question,
                        topK
                );

        return ResponseEntity.ok(results);
    }
}