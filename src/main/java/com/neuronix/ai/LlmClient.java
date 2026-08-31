package com.neuronix.ai;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface LlmClient {

    String generateResponse(List<Message> messages);
}