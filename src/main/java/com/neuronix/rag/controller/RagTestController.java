package com.neuronix.rag.controller;

import com.neuronix.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag/test")
@RequiredArgsConstructor
public class RagTestController {

    private final RagService ragService;

    @GetMapping("/documents/{documentId}/ask")
    public ResponseEntity<String> askQuestion(
            @PathVariable Long documentId,
            @RequestParam String question,
            @RequestParam(defaultValue = "5") int topK
    ) {

        String answer =
                ragService.answerQuestion(
                        documentId,
                        question,
                        topK
                );

        return ResponseEntity.ok(answer);
    }
}