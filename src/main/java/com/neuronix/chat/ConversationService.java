package com.neuronix.chat;

import com.neuronix.chat.dto.ConversationResponse;
import com.neuronix.chat.dto.MessageResponse;
import com.neuronix.exception.ConversationNotFoundException;
import com.neuronix.security.CurrentUserService;
import com.neuronix.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final CurrentUserService currentUserService;
    private final MessageRepository messageRepository;

    public List<ConversationResponse> getMyConversations() {

        User user = currentUserService.getCurrentUser();

        List<Conversation> conversations =
                conversationRepository.findByUserOrderByUpdatedAtDesc(user);

        return conversations.stream()
                .map(conversation -> new ConversationResponse(
                        conversation.getId(),
                        conversation.getTitle(),
                        conversation.getCreatedAt(),
                        conversation.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
    public List<MessageResponse> getConversationMessages(Long conversationId) {

        User user = currentUserService.getCurrentUser();

        Conversation conversation = conversationRepository
                .findByIdAndUser(conversationId, user)
                .orElseThrow(() ->
                        new ConversationNotFoundException(
                                "Conversation not found"
                        )

                );

        return messageRepository
                .findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getRole(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
    @Transactional
    public String deleteConversation(Long conversationId) {

        User user = currentUserService.getCurrentUser();

        Conversation conversation = conversationRepository
                .findByIdAndUser(conversationId, user)
                .orElseThrow(() ->
                        new ConversationNotFoundException(
                                "Conversation not found"
                        )
                );

        conversationRepository.delete(conversation);

        return "Deletion successful";
    }
}