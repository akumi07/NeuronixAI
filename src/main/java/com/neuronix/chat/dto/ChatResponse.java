package com.neuronix.chat.dto;

public record ChatResponse(
        Long conversationId,
        String response
) {
}