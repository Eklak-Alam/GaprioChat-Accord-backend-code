package com.gaprio.repository;

import com.gaprio.entities.Conversation;
import com.gaprio.entities.ConversationMember;
import com.gaprio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {
    List<ConversationMember> findByConversation(Conversation conversation);
    List<ConversationMember> findByUser(User user);
    Optional<ConversationMember> findByConversationAndUser(Conversation conversation, User user);
}
