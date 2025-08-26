package com.gaprio.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.UUID;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageMention {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private ChatMessage message;

    @ManyToOne
    @JoinColumn(name = "mentioned_user_id")
    private User mentionedUser;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public User getMentionedUser() {
        return mentionedUser;
    }

    public void setMentionedUser(User mentionedUser) {
        this.mentionedUser = mentionedUser;
    }
}
