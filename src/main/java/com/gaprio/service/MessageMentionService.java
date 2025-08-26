package com.gaprio.service;

import com.gaprio.entities.ChatMessage;
import com.gaprio.entities.MessageMention;
import com.gaprio.entities.User;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.ChatMessageRepository;
import com.gaprio.repository.MessageMentionRepository;
import com.gaprio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Create and query message mentions (@user).
 */
@Service
@Transactional
public class MessageMentionService {

    private final MessageMentionRepository mentionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageMentionService(MessageMentionRepository mentionRepository,
                                 ChatMessageRepository messageRepository,
                                 UserRepository userRepository) {
        this.mentionRepository = mentionRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Persist mentions for a message given a list of mentioned user ids.
     */
    public List<MessageMention> createMentions(UUID messageId, List<UUID> mentionedUserIds) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId.toString()));

        List<MessageMention> saved = new ArrayList<>();
        if (mentionedUserIds == null) return saved;

        for (UUID uid : mentionedUserIds) {
            User u = userRepository.findById(uid).orElse(null);
            if (u == null) continue;
            MessageMention mm = MessageMention.builder()
                    .message(msg)
                    .mentionedUser(u)
                    .build();
            saved.add(mentionRepository.save(mm));
        }
        return saved;
    }

    public List<MessageMention> findMentionsForUser(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        return mentionRepository.findByMentionedUser(u);
    }
}
