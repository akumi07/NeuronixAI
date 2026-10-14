package com.neuronix.rag.service;

import com.neuronix.rag.retrieval.dto.RetrievalResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagContextBuilder {

    public String buildContext(
            List<RetrievalResult> results
    ) {

        if (results == null || results.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();

        for (int i = 0; i < results.size(); i++) {

            RetrievalResult result = results.get(i);

            context.append("--- Context ")
                    .append(i + 1)
                    .append(" ---\n");

            context.append(result.getContent())
                    .append("\n\n");
        }

        return context.toString().trim();
    }
}