package com.neuronix.document.service;

import com.neuronix.document.dto.DocumentResponse;
import com.neuronix.document.entity.Document;
import com.neuronix.document.repository.DocumentRepository;
import com.neuronix.user.User;
import com.neuronix.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;

    public DocumentResponse uploadDocument(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File must not be empty");
        }

        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }

        if (contentType != null
                && !contentType.equalsIgnoreCase("application/pdf")
                && !contentType.equalsIgnoreCase("application/octet-stream")) {
            throw new IllegalArgumentException("Only PDF files are supported");
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found")
                );

        /*
         * Generate a unique S3 object key.
         *
         * Example:
         * users/1/documents/550e8400-e29b-41d4-a716-446655440000-Akash_Mishra_SDE_Resume.pdf
         */
        String objectKey =
                "users/" +
                        user.getId() +
                        "/documents/" +
                        UUID.randomUUID() +
                        "-" +
                        fileName;

        // Upload actual PDF to S3
        String storagePath =
                s3StorageService.uploadFile(file, objectKey);

        // Save document metadata in PostgreSQL
        Document document = new Document();

        document.setUser(user);
        document.setFileName(fileName);
        document.setFileType(
                contentType != null ? contentType : "application/pdf"
        );
        document.setFileSize(file.getSize());
        document.setStoragePath(storagePath);
        document.setStatus("UPLOADED");
        document.setCreatedAt(LocalDateTime.now());
        document.setUpdatedAt(LocalDateTime.now());

        Document savedDocument = documentRepository.save(document);

        return DocumentResponse.builder()
                .id(savedDocument.getId())
                .fileName(savedDocument.getFileName())
                .fileType(savedDocument.getFileType())
                .fileSize(savedDocument.getFileSize())
                .status(savedDocument.getStatus())
                .createdAt(savedDocument.getCreatedAt())
                .updatedAt(savedDocument.getUpdatedAt())
                .build();
    }
}