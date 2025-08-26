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

/**
 * Manage members of a conversation: add, remove, list and role updates.
 */
@Service
@Transactional
public class ConversationMemberService {

    private final ConversationMemberRepository memberRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationMemberService(ConversationMemberRepository memberRepository,
                                     ConversationRepository conversationRepository,
                                     UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Add a member to a conversation with a role (OWNER/ADMIN/MEMBER).
     */
    public ConversationMember addMember(UUID convId, UUID userId, String role) {
        Conversation conv = conversationRepository.findById(convId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", convId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        // Avoid duplicate membership
        Optional<ConversationMember> existing = memberRepository.findByConversationAndUser(conv, user);
        if (existing.isPresent()) {
            throw new BadRequestException("User is already a member of the conversation");
        }

        ConversationMember member = ConversationMember.builder()
                .conversation(conv)
                .user(user)
                .role(role == null ? "MEMBER" : role)
                .joinedAt(System.currentTimeMillis())
                .build();

        return memberRepository.save(member);
    }

    /**
     * Remove a member from conversation.
     */
    public void removeMember(UUID convId, UUID userId) {
        Conversation conv = conversationRepository.findById(convId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", convId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        ConversationMember cm = memberRepository.findByConversationAndUser(conv, user)
                .orElseThrow(() -> new BadRequestException("User is not member of conversation"));

        memberRepository.delete(cm);
    }

    /**
     * List members of a conversation.
     */
    public List<ConversationMember> listMembers(UUID convId) {
        Conversation conv = conversationRepository.findById(convId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", convId.toString()));
        return memberRepository.findByConversation(conv);
    }

    /**
     * Check if user is member of conversation.
     */
    public boolean isMember(UUID convId, UUID userId) {
        Conversation conv = conversationRepository.findById(convId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", convId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        return memberRepository.findByConversationAndUser(conv, user).isPresent();
    }
}
