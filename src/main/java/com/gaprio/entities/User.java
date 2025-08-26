package com.gaprio.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private UUID id;

    private String name;
    private String username;   // unique username for mentions (@username)
    private String email;
    private String password;

    // Default role = USER
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private String avatarUrl;  // profile picture
    private String about;      // bio/status message
    private Long lastSeenAt;   // for online/offline tracking

    // === RELATIONSHIPS ===

    // Conversations the user is part of
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationMember> conversations = new ArrayList<>();

    // Messages sent by this user
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    // Message receipts (delivered/read)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageReceipt> receipts = new ArrayList<>();

    // Mentions (when this user is mentioned in a message)
    @OneToMany(mappedBy = "mentionedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MessageMention> mentions = new ArrayList<>();

    // Attachments uploaded by this user
    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    // Refresh tokens (JWT)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Long getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Long lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public List<ConversationMember> getConversations() {
        return conversations;
    }

    public void setConversations(List<ConversationMember> conversations) {
        this.conversations = conversations;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<MessageReceipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<MessageReceipt> receipts) {
        this.receipts = receipts;
    }

    public List<MessageMention> getMentions() {
        return mentions;
    }

    public void setMentions(List<MessageMention> mentions) {
        this.mentions = mentions;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<RefreshToken> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }
}
