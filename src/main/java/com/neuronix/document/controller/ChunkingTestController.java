package com.neuronix.document.controller;

import com.neuronix.document.entity.Document;
import com.neuronix.document.entity.DocumentChunk;
import com.neuronix.document.repository.DocumentRepository;
import com.neuronix.document.service.DocumentChunkingService;
import com.neuronix.document.service.PdfTextExtractorService;
import com.neuronix.document.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents/test")
@RequiredArgsConstructor
public class ChunkingTestController {

    private final DocumentRepository documentRepository;
    private final S3StorageService s3StorageService;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final DocumentChunkingService documentChunkingService;

    @PostMapping("/{documentId}/chunk")
    public ResponseEntity<String> chunkDocument(
            @PathVariable Long documentId
    ) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Document not found")
                );

        if (document.getStoragePath() == null) {
            throw new IllegalArgumentException(
                    "Document does not have an S3 storage path"
            );
        }

        byte[] pdfBytes = s3StorageService.downloadFile(
                document.getStoragePath()
        );

        String text = pdfTextExtractorService.extractText(
                pdfBytes
        );

        List<DocumentChunk> chunks =
                documentChunkingService.chunkDocument(
                        document,
                        text
                );

        return ResponseEntity.ok(
                "Created " + chunks.size() + " chunks"
        );
    }
}