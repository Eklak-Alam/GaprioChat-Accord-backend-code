package com.gaprio.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private UUID id;
    private String chatName; // group name or "private"
    private boolean isGroup;
    private List<UserResponse> participants; // users in chat
    private MessageResponse lastMessage; // last message in the chat

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public List<UserResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserResponse> participants) {
        this.participants = participants;
    }

    public MessageResponse getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageResponse lastMessage) {
        this.lastMessage = lastMessage;
    }
}
