package com.neuronix.document.repository;

import com.neuronix.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUserId(Long userId);
}