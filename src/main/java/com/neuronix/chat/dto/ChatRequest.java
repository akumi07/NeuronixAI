package com.neuronix.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(

        Long conversationId,

        @NotBlank(message = "Message is required")
        String message

) {
}