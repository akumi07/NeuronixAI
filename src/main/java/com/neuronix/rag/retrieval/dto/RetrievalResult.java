package com.neuronix.rag.retrieval.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetrievalResult {

    private Long chunkId;

    private Long documentId;

    private Integer chunkIndex;

    private String content;

    private Double similarity;
}