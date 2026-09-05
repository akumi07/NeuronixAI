package com.neuronix.document.controller;

import com.neuronix.document.dto.DocumentResponse;
import com.neuronix.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file) {

        DocumentResponse response = documentService.uploadDocument(file);

        return ResponseEntity.ok(response);
    }
}