package com.gaprio.service;

import com.gaprio.entities.Conversation;
import com.gaprio.entities.ConversationMember;
import com.gaprio.entities.User;
import com.gaprio.exceptions.BadRequestException;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.ConversationMemberRepository;
import com.gaprio.repository.ConversationRepository;
import com.gaprio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Conversation-level business logic: create direct conversations, create groups,
 * list conversations for a user, get details, etc.
 */
@Service
@Transactional
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final UserRepository userRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationMemberRepository memberRepository,
                               UserRepository userRepository) {
        this.conversationRepository = conversationRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create or return existing DIRECT conversation between userA and userB.
     * Ensures only one DIRECT conversation exists between two users.
     */
    public Conversation createOrGetDirectConversation(UUID userAId, UUID userBId) {
        if (userAId == null || userBId == null) {
            throw new BadRequestException("Both user IDs are required for direct conversation");
        }
        if (userAId.equals(userBId)) {
            throw new BadRequestException("Cannot create direct conversation with same user");
        }

        User userA = userRepository.findById(userAId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userAId.toString()));
        User userB = userRepository.findById(userBId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userBId.toString()));

        // Find intersection of conversation ids for both users
        Set<UUID> convsA = memberRepository.findByUser(userA)
                .stream().map(cm -> cm.getConversation().getId()).collect(Collectors.toSet());
        Set<UUID> convsB = memberRepository.findByUser(userB)
                .stream().map(cm -> cm.getConversation().getId()).collect(Collectors.toSet());

        convsA.retainAll(convsB); // intersection

        for (UUID convId : convsA) {
            Conversation conv = conversationRepository.findById(convId).orElse(null);
            if (conv != null && "DIRECT".equalsIgnoreCase(conv.getType())) {
                // Ensure conversation has exactly two members (defensive)
                if (conv.getMembers() != null && conv.getMembers().size() == 2) {
                    return conv;
                }
            }
        }

        // Not found -> create new DIRECT conversation
        Conversation conv = Conversation.builder()
                .type("DIRECT")
                .title(null)
                .createdBy(userAId)
                .createdAt(System.currentTimeMillis())
                .build();

        conv = conversationRepository.save(conv);

        // add two members
        ConversationMember memberA = ConversationMember.builder()
                .conversation(conv)
                .user(userA)
                .role("MEMBER")
                .joinedAt(System.currentTimeMillis())
                .build();
        memberRepository.save(memberA);

        ConversationMember memberB = ConversationMember.builder()
                .conversation(conv)
                .user(userB)
                .role("MEMBER")
                .joinedAt(System.currentTimeMillis())
                .build();
        memberRepository.save(memberB);

        return conv;
    }

    /**
     * Create a group conversation (type = GROUP). creatorId must be in memberIds too.
     */
    public Conversation createGroupConversation(String title, UUID creatorId, List<UUID> initialMemberIds) {
        if (creatorId == null) throw new BadRequestException("Creator ID is required");
        if (title == null || title.trim().isEmpty()) throw new BadRequestException("Group title is required");

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", creatorId.toString()));

        Conversation conv = Conversation.builder()
                .type("GROUP")
                .title(title.trim())
                .createdBy(creatorId)
                .createdAt(System.currentTimeMillis())
                .build();
        conv = conversationRepository.save(conv);

        // Ensure creator is a member and owner
        ConversationMember ownerMember = ConversationMember.builder()
                .conversation(conv)
                .user(creator)
                .role("OWNER")
                .joinedAt(System.currentTimeMillis())
                .build();
        memberRepository.save(ownerMember);

        // add other members if provided
        if (initialMemberIds != null) {
            for (UUID mid : initialMemberIds) {
                if (mid.equals(creatorId)) continue;
                User u = userRepository.findById(mid).orElse(null);
                if (u == null) continue; // skip unknown users
                ConversationMember m = ConversationMember.builder()
                        .conversation(conv)
                        .user(u)
                        .role("MEMBER")
                        .joinedAt(System.currentTimeMillis())
                        .build();
                memberRepository.save(m);
            }
        }

        return conv;
    }

    /**
     * Return conversation by id or throw.
     */
    public Conversation getConversation(UUID convId) {
        return conversationRepository.findById(convId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", convId.toString()));
    }

    /**
     * Return all conversations for a user (by membership).
     */
    public List<Conversation> getUserConversations(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        List<ConversationMember> members = memberRepository.findByUser(u);
        return members.stream().map(ConversationMember::getConversation).collect(Collectors.toList());
    }
}
