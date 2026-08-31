package com.neuronix.chat;

import com.neuronix.ai.LlmClient;
import com.neuronix.chat.dto.ChatResponse;
import com.neuronix.security.CurrentUserService;
import com.neuronix.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
                    generateConversationTitle(message)
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

        List<Message> history =
                messageRepository
                        .findByConversationOrderByCreatedAtAsc(conversation);

        List<org.springframework.ai.chat.messages.Message> aiMessages =
                new ArrayList<>();

        for (Message historyMessage : history) {

            if ("USER".equals(historyMessage.getRole())) {

                aiMessages.add(
                        new UserMessage(historyMessage.getContent())
                );

            } else if ("ASSISTANT".equals(historyMessage.getRole())) {

                aiMessages.add(
                        new AssistantMessage(historyMessage.getContent())
                );
            }
        }

        String response = llmClient.generateResponse(aiMessages);

        Message assistantMessage = new Message(
                conversation,
                "ASSISTANT",
                response
        );

        messageRepository.save(assistantMessage);

        conversation.updateTimestamp();
        conversationRepository.save(conversation);

        return new ChatResponse(
                conversation.getId(),
                response
        );
    }

    private String generateConversationTitle(String message) {

        String title = message.trim();

        if (title.length() > 50) {
            title = title.substring(0, 50).trim() + "...";
        }

        return title;
    }
}