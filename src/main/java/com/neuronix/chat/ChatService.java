package com.neuronix.chat;

import com.neuronix.ai.LlmClient;
import com.neuronix.chat.dto.ChatResponse;
import com.neuronix.security.CurrentUserService;
import com.neuronix.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final LlmClient llmClient;
    private final CurrentUserService currentUserService;
    private final MessageRepository messageRepository;

    private final ConversationRepository conversationRepository;
    public ChatResponse chat(Long conversationId, String message) {

        User user = currentUserService.getCurrentUser();

        Conversation conversation;

        if (conversationId == null) {

            conversation = new Conversation(
                    user,
                    null
            );

            conversation = conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository
                    .findByIdAndUser(conversationId, user)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Conversation not found"
                            )
                    );
        }
        Message userMessage = new Message(
                conversation,
                "USER",
                message
        );

        messageRepository.save(userMessage);

        String response = llmClient.generateResponse(message);
        Message assistantMessage = new Message(
                conversation,
                "ASSISTANT",
                response
        );

        messageRepository.save(assistantMessage);

        return new ChatResponse(response);
    }
}