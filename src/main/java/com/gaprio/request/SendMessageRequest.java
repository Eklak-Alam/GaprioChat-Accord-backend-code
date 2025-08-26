package com.gaprio.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private UUID conversationId;     // target conversation (DIRECT/GROUP)
    private UUID senderId;           // sender user id
    private String type;             // e.g. "TEXT", "IMAGE", "FILE"
    private String content;          // text content or JSON payload for media
    private List<UUID> mentionIds;   // optional @mentions
    private UUID attachmentId;       // optional attachment id

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<UUID> getMentionIds() {
        return mentionIds;
    }

    public void setMentionIds(List<UUID> mentionIds) {
        this.mentionIds = mentionIds;
    }

    public UUID getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(UUID attachmentId) {
        this.attachmentId = attachmentId;
    }
}