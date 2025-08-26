package com.gaprio.repository;

import com.gaprio.entities.ChatMessage;
import com.gaprio.entities.MessageMention;
import com.gaprio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageMentionRepository extends JpaRepository<MessageMention, UUID> {
    List<MessageMention> findByMessage(ChatMessage message);
    List<MessageMention> findByMentionedUser(User user);
}
