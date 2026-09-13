package com.neuronix.rag.retrieval.repository;

import com.neuronix.rag.retrieval.dto.RetrievalResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DocumentRetrievalRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<RetrievalResult> findSimilarChunks(
            Long documentId,
            float[] queryEmbedding,
            int topK
    ) {

        String sql = """
                SELECT
                    id,
                    document_id,
                    chunk_index,
                    content,
                    1 - (embedding <=> CAST(? AS vector)) AS similarity
                FROM document_chunks
                WHERE document_id = ?
                  AND embedding IS NOT NULL
                ORDER BY embedding <=> CAST(? AS vector)
                LIMIT ?
                """;

        String vector = toVectorString(queryEmbedding);

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> RetrievalResult.builder()
                        .chunkId(rs.getLong("id"))
                        .documentId(rs.getLong("document_id"))
                        .chunkIndex(rs.getInt("chunk_index"))
                        .content(rs.getString("content"))
                        .similarity(rs.getDouble("similarity"))
                        .build(),
                vector,
                documentId,
                vector,
                topK
        );
    }

    private String toVectorString(float[] embedding) {

        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < embedding.length; i++) {

            if (i > 0) {
                builder.append(",");
            }

            builder.append(embedding[i]);
        }

        builder.append("]");

        return builder.toString();
    }
}