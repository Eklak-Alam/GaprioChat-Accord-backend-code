package com.gaprio.service;

import com.gaprio.entities.ChatMessage;
import com.gaprio.entities.Conversation;
import com.gaprio.entities.ConversationMember;
import com.gaprio.entities.MessageReceipt;
import com.gaprio.entities.User;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.ChatMessageRepository;
import com.gaprio.repository.ConversationMemberRepository;
import com.gaprio.repository.MessageReceiptRepository;
import com.gaprio.repository.ConversationRepository;
import com.gaprio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Handles creation and updating of message receipts (SENT, DELIVERED, READ).
 */
@Service
@Transactional
public class MessageReceiptService {

    private final MessageReceiptRepository receiptRepository;
    private final ConversationMemberRepository memberRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageReceiptService(MessageReceiptRepository receiptRepository,
                                 ConversationMemberRepository memberRepository,
                                 ConversationRepository conversationRepository,
                                 ChatMessageRepository messageRepository,
                                 UserRepository userRepository) {
        this.receiptRepository = receiptRepository;
        this.memberRepository = memberRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create initial receipts for a newly persisted message.
     * Status initially -> SENT for each member (including sender).
     */
    public List<MessageReceipt> createInitialReceipts(UUID messageId) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId.toString()));
        Conversation conv = msg.getConversation();
        List<ConversationMember> members = memberRepository.findByConversation(conv);
        List<MessageReceipt> receipts = new ArrayList<>();
        for (ConversationMember cm : members) {
            MessageReceipt r = MessageReceipt.builder()
                    .message(msg)
                    .user(cm.getUser())
                    .status("SENT")
                    .updatedAt(System.currentTimeMillis())
                    .build();
            receipts.add(receiptRepository.save(r));
        }
        return receipts;
    }

    /**
     * Update receipt for (messageId, userId) to status (DELIVERED or READ).
     */
    public MessageReceipt updateReceipt(UUID messageId, UUID userId, String status) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        List<MessageReceipt> receipts = receiptRepository.findByMessage(msg);
        for (MessageReceipt r : receipts) {
            if (r.getUser().getId().equals(userId)) {
                r.setStatus(status);
                r.setUpdatedAt(System.currentTimeMillis());
                return receiptRepository.save(r);
            }
        }

        // If receipt not found (shouldn't happen normally), create one
        MessageReceipt r = MessageReceipt.builder()
                .message(msg)
                .user(user)
                .status(status)
                .updatedAt(System.currentTimeMillis())
                .build();
        return receiptRepository.save(r);
    }

    /**
     * Get receipts for a message.
     */
    public List<MessageReceipt> getReceiptsForMessage(UUID messageId) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId.toString()));
        return receiptRepository.findByMessage(msg);
    }
}
