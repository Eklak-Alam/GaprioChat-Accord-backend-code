package com.gaprio.mapper;

import com.gaprio.entities.ChatMessage;
import com.gaprio.entities.Conversation;
import com.gaprio.entities.ConversationMember;
import com.gaprio.response.ChatResponse;
import com.gaprio.response.MessageResponse;
import com.gaprio.response.UserResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChatResponseMapper {
    private ChatResponseMapper() {}

    public static ChatResponse toDto(Conversation c) {
        boolean isGroup = "GROUP".equalsIgnoreCase(c.getType());
        String chatName = isGroup ? (c.getTitle() == null ? "Group" : c.getTitle()) : "DIRECT";

        // participants
        List<UserResponse> participants = c.getMembers() == null ? List.of() :
                c.getMembers().stream()
                        .map(ConversationMember::getUser)
                        .filter(Objects::nonNull)
                        .map(u -> new UserResponse(
                                u.getId(),
                                u.getName(),
                                u.getUsername(),
                                u.getEmail(),
                                u.getRole(),
                                u.getAvatarUrl(),
                                u.getAbout(),
                                u.getLastSeenAt()
                        ))
                        .collect(Collectors.toList());

        // last message — assume conversation has messages list or fetch lazily if needed
        MessageResponse last = null;
        if (c.getMessages() != null && !c.getMessages().isEmpty()) {
            ChatMessage lm = c.getMessages()
                    .stream()
                    .filter(m -> m.getDeletedAt() == null)
                    .max((a, b) -> Long.compare(a.getCreatedAt(), b.getCreatedAt()))
                    .orElse(null);
            if (lm != null) last = MessageMapper.toDto(lm);
        }

        ChatResponse dto = new ChatResponse();
        dto.setId(c.getId());
        dto.setChatName(chatName);
        dto.setGroup(isGroup);
        dto.setParticipants(participants);
        dto.setLastMessage(last);
        return dto;
    }

    public static List<ChatResponse> toDtos(List<Conversation> conversations) {
        return conversations.stream().map(ChatResponseMapper::toDto).collect(Collectors.toList());
    }
}
