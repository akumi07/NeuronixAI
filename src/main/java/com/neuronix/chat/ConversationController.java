package com.neuronix.chat;

import com.neuronix.chat.dto.ConversationResponse;
import com.neuronix.chat.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationResponse> getMyConversations() {
        return conversationService.getMyConversations();
    }

    @GetMapping("/{conversationId}/messages")
    public List<MessageResponse> getConversationMessages(
            @PathVariable Long conversationId
    ) {
        return conversationService.getConversationMessages(conversationId);
    }

    // get-message-by-id

    @GetMapping("/{conversationId}/messages/{messageId}")
    public MessageResponse getMessageById(
            @PathVariable Long conversationId,
            @PathVariable Long messageId
    ) {
        return conversationService.getMessageById(
                conversationId,
                messageId
        );
    }

//delete controler
    @DeleteMapping("/{conversationId}/deleteConversation")
    public String deleteConversation(
            @PathVariable Long conversationId
    ) {
        return conversationService.deleteConversation(conversationId);
    }
}