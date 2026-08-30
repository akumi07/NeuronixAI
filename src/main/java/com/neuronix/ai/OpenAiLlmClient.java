package com.neuronix.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class OpenAiLlmClient implements LlmClient {

    private final ChatClient chatClient;

    public OpenAiLlmClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String generateResponse(String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }
}