package com.neuronix.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import com.neuronix.user.User;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(
            Conversation conversation
    );


    Optional<Message> findByIdAndConversationIdAndConversationUser(
            Long messageId,
            Long conversationId,
            User user
    );
}