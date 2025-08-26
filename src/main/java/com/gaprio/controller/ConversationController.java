package com.gaprio.controller;

import com.gaprio.entities.Conversation;
import com.gaprio.mapper.ChatResponseMapper;
import com.gaprio.mapper.GroupResponseMapper;
import com.gaprio.request.CreateChatRequest;
import com.gaprio.request.CreateGroupRequest;
import com.gaprio.response.ChatResponse;
import com.gaprio.response.GroupResponse;
import com.gaprio.service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller for chats and groups (conversations).
 */
@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "http://localhost:3000")
public class ConversationController {

    private final ConversationService conversationService;
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    // Create direct chat between two users
    @PostMapping("/direct")
    public ResponseEntity<ChatResponse> createDirectChat(@RequestBody CreateChatRequest req) {
        // FIX: your request used Long; it must be UUID for your services.
        UUID u1 = req.getUserId1();
        UUID u2 = req.getUserId2();

        Conversation conv = conversationService.createOrGetDirectConversation(u1, u2);
        return ResponseEntity.ok(ChatResponseMapper.toDto(conv));
    }

    // Create group chat
    @PostMapping("/group")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody CreateGroupRequest req) {
        Conversation conv = conversationService.createGroupConversation(
                req.getGroupName(),
                req.getCreatorId(),
                req.getMemberIds()
        );
        return ResponseEntity.ok(GroupResponseMapper.toDto(conv));
    }

    // List conversations for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatResponse>> listUserConversations(@PathVariable UUID userId) {
        List<Conversation> convs = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(convs.stream().map(ChatResponseMapper::toDto).collect(Collectors.toList()));
    }
}
