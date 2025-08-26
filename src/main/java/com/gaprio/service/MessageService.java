package com.gaprio.service;

import com.gaprio.entities.*;
import com.gaprio.exceptions.BadRequestException;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Core message operations: send, fetch (pagination), edit, delete, mark read.
 */
@Service
@Transactional
public class MessageService {

    private final ChatMessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final MessageMentionService mentionService;
    private final MessageReceiptService receiptService;
    private final ConversationMemberRepository memberRepository;

    public MessageService(ChatMessageRepository messageRepository,
                          ConversationRepository conversationRepository,
                          UserRepository userRepository,
                          AttachmentRepository attachmentRepository,
                          MessageMentionService mentionService,
                          MessageReceiptService receiptService,
                          ConversationMemberRepository memberRepository) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.attachmentRepository = attachmentRepository;
        this.mentionService = mentionService;
        this.receiptService = receiptService;
        this.memberRepository = memberRepository;
    }

    /**
     * Send a message to a conversation: persist message, create receipts, save mentions and return persisted entity.
     *
     * @param conversationId UUID of conversation
     * @param senderId       UUID of sender
     * @param type           message type (TEXT, IMAGE, FILE, etc.)
     * @param content        message content (text or JSON for media)
     * @param mentionIds     list of user ids mentioned (optional)
     * @param attachmentId   attachment id (optional)
     * @return persisted ChatMessage
     */
    public ChatMessage sendMessage(UUID conversationId, UUID senderId, String type, String content,
                                   List<UUID> mentionIds, UUID attachmentId) {

        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId.toString()));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId.toString()));

        // Validate membership: sender must be a member
        boolean isMember = memberRepository.findByConversation(conv).stream()
                .anyMatch(cm -> cm.getUser().getId().equals(senderId));
        if (!isMember) {
            throw new BadRequestException("Sender is not a member of the conversation");
        }

        ChatMessage msg = ChatMessage.builder()
                .conversation(conv)
                .sender(sender)
                .type(type)
                .content(content)
                .createdAt(System.currentTimeMillis())
                .build();

        if (attachmentId != null) {
            Attachment att = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId.toString()));
            msg.setAttachment(att);
        }

        // persist message
        msg = messageRepository.save(msg);

        // create mentions (if any)
        if (mentionIds != null && !mentionIds.isEmpty()) {
            mentionService.createMentions(msg.getId(), mentionIds);
        }

        // create receipts for members (initial state = SENT)
        receiptService.createInitialReceipts(msg.getId());

        return msg;
    }

    /**
     * Get messages for a conversation with naive pagination: 'before' is a timestamp (exclusive).
     * Returns newest messages first (descending by createdAt) limited by 'limit'.
     * NOTE: for production use DB-side pagination.
     */
    public List<ChatMessage> getMessages(UUID conversationId, int limit, Long beforeTimestamp) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId.toString()));

        List<ChatMessage> allMessages = messageRepository.findByConversationOrderByCreatedAtAsc(conv);

        // Filter by 'before' timestamp (if provided)
        List<ChatMessage> filtered = allMessages.stream()
                .filter(m -> beforeTimestamp == null || m.getCreatedAt() < beforeTimestamp)
                .sorted(Comparator.comparingLong(ChatMessage::getCreatedAt).reversed()) // newest first
                .limit(limit > 0 ? limit : 50)
                .collect(Collectors.toList());

        return filtered;
    }

    /**
     * Edit a message content. Only sender can edit (this method does not check time limits).
     */
    public ChatMessage editMessage(UUID messageId, UUID editorId, String newContent) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId.toString()));

        if (!msg.getSender().getId().equals(editorId)) {
            throw new BadRequestException("Only sender can edit message");
        }

        msg.setContent(newContent);
        msg.setEditedAt(System.currentTimeMillis());
        return messageRepository.save(msg);
    }

    /**
     * Soft delete a message (set deletedAt). User can be sender or conversation admin/owner — implement checks as needed.
     */
    public ChatMessage deleteMessage(UUID messageId, UUID operatorId) {
        ChatMessage msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatMessage", "id", messageId.toString()));

        // If operator is sender -> allow. For admins/owners you'd check roles (left as TODO if needed).
        if (!msg.getSender().getId().equals(operatorId)) {
            // You can expand: check if operator is OWNER/ADMIN in conversation
            throw new BadRequestException("Only sender can delete message (or admin logic can be added)");
        }

        msg.setDeletedAt(System.currentTimeMillis());
        return messageRepository.save(msg);
    }

    /**
     * Mark a list of messages as READ for a given user.
     */
    public void markMessagesRead(UUID conversationId, UUID userId, List<UUID> messageIds) {
        // validate membership
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId.toString()));

        boolean isMember = memberRepository.findByConversation(conv).stream()
                .anyMatch(cm -> cm.getUser().getId().equals(userId));
        if (!isMember) {
            throw new BadRequestException("User is not member of the conversation");
        }

        if (messageIds == null || messageIds.isEmpty()) return;

        for (UUID mid : messageIds) {
            // update receipt to READ
            receiptService.updateReceipt(mid, userId, "READ");
        }
    }

    /**
     * Count unread messages for a user in a conversation.
     */
    public long countUnread(UUID conversationId, UUID userId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId.toString()));

        List<ChatMessage> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conv);
        long unread = 0;
        for (ChatMessage m : messages) {
            List<MessageReceipt> receipts = receiptService.getReceiptsForMessage(m.getId());
            boolean found = false;
            for (MessageReceipt r : receipts) {
                if (r.getUser().getId().equals(userId)) {
                    found = true;
                    if (!"READ".equalsIgnoreCase(r.getStatus())) unread++;
                    break;
                }
            }
            if (!found) unread++; // defensive: no receipt => unread
        }
        return unread;
    }
}

