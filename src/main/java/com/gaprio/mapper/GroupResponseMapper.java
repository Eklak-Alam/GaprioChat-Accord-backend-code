package com.gaprio.mapper;
import com.gaprio.entities.Conversation;
import com.gaprio.entities.ConversationMember;
import com.gaprio.response.GroupResponse;
import com.gaprio.response.MessageResponse;
import com.gaprio.response.UserResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupResponseMapper {
    private GroupResponseMapper() {}

    public static GroupResponse toDto(Conversation c) {
        GroupResponse dto = new GroupResponse();
        dto.setId(c.getId());
        dto.setGroupName(c.getTitle());

        // createdBy is a UUID in Conversation.createdBy; expose as String username if you want
        dto.setCreatedBy(c.getCreatedBy() == null ? null : c.getCreatedBy().toString());

        List<UserResponse> members = c.getMembers() == null ? List.of() :
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
                        )).collect(Collectors.toList());
        dto.setMembers(members);

        if (c.getMessages() != null && !c.getMessages().isEmpty()) {
            dto.setLastMessage(
                    c.getMessages().stream()
                            .filter(m -> m.getDeletedAt() == null)
                            .max((a, b) -> Long.compare(a.getCreatedAt(), b.getCreatedAt()))
                            .map(MessageMapper::toDto)
                            .orElse(null)
            );
        }
        return dto;
    }
}
