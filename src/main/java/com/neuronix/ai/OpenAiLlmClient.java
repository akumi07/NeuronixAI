package com.neuronix.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenAiLlmClient implements LlmClient {

    private final ChatClient chatClient;

    public OpenAiLlmClient(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }
    // rag task will start

    @Override
    public String generateResponse(List<Message> messages) {

        return chatClient
                .prompt()
                .messages(messages)
                .call()
                .content();
    }
}