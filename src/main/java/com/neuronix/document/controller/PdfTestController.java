package com.neuronix.document.controller;

import com.neuronix.document.service.PdfTextExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/documents/test")
@RequiredArgsConstructor
public class PdfTestController {

    private final PdfTextExtractorService pdfTextExtractorService;

    @PostMapping("/extract-text")
    public ResponseEntity<String> extractText(
            @RequestParam("file") MultipartFile file
    ) {

        try {
            String text = pdfTextExtractorService.extractText(
                    file.getBytes()
            );

            return ResponseEntity.ok(text);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to extract PDF text");
        }
    }
}