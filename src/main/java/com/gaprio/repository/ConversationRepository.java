package com.gaprio.repository;

import com.gaprio.entities.Conversation;
import com.gaprio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findByCreatedBy(UUID userId);
    List<Conversation> findByType(String type); // DIRECT or GROUP
}
