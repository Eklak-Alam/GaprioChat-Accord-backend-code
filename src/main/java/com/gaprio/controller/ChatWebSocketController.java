package com.gaprio.controller;
import com.gaprio.entities.ChatMessage;
import com.gaprio.mapper.MessageMapper;
import com.gaprio.request.SendMessageRequest;
import com.gaprio.response.MessageResponse;
import com.gaprio.service.MessageService;
import com.gaprio.websocket.MessagePublisher;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * WebSocket controller for real-time chat over STOMP.
 *
 * Client sends to: /app/chat.sendMessage
 * We publish to:   /topic/conversation.{conversationId}
 */
@Controller
@CrossOrigin(origins = "http://localhost:3000")
public class ChatWebSocketController {

    private final MessageService messageService;
    private final MessagePublisher publisher;

    public ChatWebSocketController(MessageService messageService, MessagePublisher publisher) {
        this.messageService = messageService;
        this.publisher = publisher;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(SendMessageRequest req) {
        ChatMessage saved = messageService.sendMessage(
                req.getConversationId(),
                req.getSenderId(),
                req.getType(),
                req.getContent(),
                req.getMentionIds(),
                req.getAttachmentId()
        );
        MessageResponse dto = MessageMapper.toDto(saved);

        // Broadcast only to that conversation’s topic
        publisher.toConversation(saved.getConversation().getId(), dto);
    }
}
