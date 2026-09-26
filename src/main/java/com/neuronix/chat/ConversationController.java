package com.neuronix.chat;

import com.neuronix.chat.dto.ConversationResponse;
import com.neuronix.chat.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationResponse> getMyConversations() {

        //System.out.println("Controller reached");
        return conversationService.getMyConversations();
    }
    @GetMapping("/{conversationId}/messages")
    public List<MessageResponse> getConversationMessages(
            @PathVariable Long conversationId
    ) {
        //System.out.println("Controller reached with ID: " + conversationId);
        return conversationService.getConversationMessages(conversationId);
    }

    @GetMapping("/{conversationId}/messages/{messageId}")
    public MessageResponse getMessageById(
            @PathVariable Long conversationId,
            @PathVariable Long messageId) {

        return conversationService.getMessageById(conversationId, messageId);
    }

    @DeleteMapping("/{conversationId}")
    public String deleteConversation(@PathVariable Long conversationId){

        return conversationService.deleteConversation(conversationId);
    }
}

