package com.gaprio.mapper;

import com.gaprio.entities.ChatMessage;
import com.gaprio.response.MessageResponse;

public class MessageMapper {
    private MessageMapper() {}

    public static MessageResponse toDto(ChatMessage m) {
        return new MessageResponse(
                m.getId(),
                m.getConversation().getId(),
                m.getSender().getId(),
                m.getSender().getUsername(),
                m.getType(),
                m.getContent(),
                m.getCreatedAt(),
                m.getEditedAt(),
                m.getDeletedAt()
        );
    }
}
