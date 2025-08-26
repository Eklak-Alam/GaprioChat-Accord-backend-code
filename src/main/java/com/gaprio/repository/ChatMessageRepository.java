package com.gaprio.repository;

import com.gaprio.entities.ChatMessage;
import com.gaprio.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}
