package com.gaprio.controller;

import com.gaprio.entities.ChatMessage;
import com.gaprio.mapper.MessageMapper;
import com.gaprio.request.SendMessageRequest;
import com.gaprio.response.MessageResponse;
import com.gaprio.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for message CRUD & history.
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:3000")
public class MessageController {

    private final MessageService messageService;
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Persist message into DB
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@RequestBody SendMessageRequest req) {
        ChatMessage msg = messageService.sendMessage(
                req.getConversationId(),
                req.getSenderId(),
                req.getType(),
                req.getContent(),
                req.getMentionIds(),
                req.getAttachmentId()
        );
        return ResponseEntity.ok(MessageMapper.toDto(msg));
    }

    // Get messages for a conversation
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable UUID conversationId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long before
    ) {
        List<ChatMessage> data = messageService.getMessages(conversationId, limit, before);
        return ResponseEntity.ok(data.stream().map(MessageMapper::toDto).collect(Collectors.toList()));
    }

    // Edit a message
    @PatchMapping("/{messageId}")
    public ResponseEntity<MessageResponse> editMessage(
            @PathVariable UUID messageId,
            @RequestParam UUID editorId,
            @RequestBody String newContent
    ) {
        ChatMessage edited = messageService.editMessage(messageId, editorId, newContent);
        return ResponseEntity.ok(MessageMapper.toDto(edited));
    }

    // Delete a message (soft delete)
    @DeleteMapping("/{messageId}")
    public ResponseEntity<MessageResponse> deleteMessage(
            @PathVariable UUID messageId,
            @RequestParam UUID operatorId
    ) {
        ChatMessage deleted = messageService.deleteMessage(messageId, operatorId);
        return ResponseEntity.ok(MessageMapper.toDto(deleted));
    }
}
