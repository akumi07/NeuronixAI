package com.neuronix.rag.service;

import com.neuronix.ai.LlmClient;
import com.neuronix.rag.retrieval.dto.RetrievalResult;
import com.neuronix.rag.retrieval.service.DocumentRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagService {

    private final DocumentRetrievalService documentRetrievalService;
    private final RagContextBuilder ragContextBuilder;
    private final LlmClient llmClient;

    public String answerQuestion(
            Long documentId,
            String question,
            int topK
    ) {

        List<RetrievalResult> results =
                documentRetrievalService.retrieveRelevantChunks(
                        documentId,
                        question,
                        topK
                );

        String context =
                ragContextBuilder.buildContext(results);

        SystemMessage systemMessage =
                new SystemMessage("""
                        You are a helpful AI assistant.

                        Answer the user's question using the provided
                        document context.

                        If the answer cannot be found in the provided
                        context, say that the information is not available
                        in the document.

                        Do not invent facts that are not supported
                        by the context.
                        """);

        UserMessage userMessage =
                new UserMessage("""
                        CONTEXT:
                        %s

                        QUESTION:
                        %s
                        """.formatted(context, question));

        List<Message> messages =
                List.of(systemMessage, userMessage);

        return llmClient.generateResponse(messages);
    }
}