package com.gaprio.websocket;

import com.gaprio.response.MessageResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper to publish messages to conversation topic channels.
 */
@Component
public class MessagePublisher {

    private final SimpMessagingTemplate simp;

    public MessagePublisher(SimpMessagingTemplate simp) {
        this.simp = simp;
    }

    public void toConversation(UUID conversationId, MessageResponse payload) {
        String destination = "/topic/conversation." + conversationId;
        simp.convertAndSend(destination, payload);
    }
}
