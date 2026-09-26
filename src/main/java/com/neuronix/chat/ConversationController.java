package com.neuronix.chat;

import com.neuronix.chat.dto.ConversationResponse;
import com.neuronix.chat.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.neuronix.chat.dto.Update_titleConversationRequest;

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

    @PatchMapping("/{conversationId}")
    public ConversationResponse updateConversation(
            @PathVariable Long conversationId,
            @RequestBody Update_titleConversationRequest request
    ) {
        return conversationService.updateConversationTitle(
                conversationId,
                request
        );
    }

}

    //delete