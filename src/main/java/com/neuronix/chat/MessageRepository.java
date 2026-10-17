package com.neuronix.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(
            Conversation conversation
    );


    Optional<Message> findByIdAndConversation(
            Long messageId,
            Conversation conversation
    );
}