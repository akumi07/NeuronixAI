package com.neuronix.chat;

import com.neuronix.ai.LlmClient;
import com.neuronix.chat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final LlmClient llmClient;

    public ChatResponse chat(String message) {

        String response = llmClient.generateResponse(message);

        return new ChatResponse(response);
    }
}