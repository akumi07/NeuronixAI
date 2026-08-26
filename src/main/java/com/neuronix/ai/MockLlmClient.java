package com.neuronix.ai;

import org.springframework.stereotype.Component;

@Component
public class MockLlmClient implements LlmClient {

    @Override
    public String generateResponse(String message) {
        return "NeuronixAI received: " + message;
    }
}