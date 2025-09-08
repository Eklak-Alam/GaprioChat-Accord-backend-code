package com.gaprio.controller;

import com.gaprio.entities.User;
import com.gaprio.response.UserResponse;
import com.gaprio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 * Exposes search and lookup APIs for chat app.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Convert User entity → UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getAvatarUrl(),
                user.getAbout(),
                user.getLastSeenAt()
        );
    }

    /**
     * Get a user by UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(mapToResponse(user));
    }

    /**
     * Get a user by username.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(mapToResponse(user));
    }

    /**
     * Search users by query (username, name, or email).
     * Example: /api/users/search?q=john&limit=10
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam(name = "q") String query,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<UserResponse> results = userService.searchByNameOrUsername(query.trim(), limit)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    /**
     * Create or update a user.
     * Note: Password encoding should be handled before saving.
     */
    @PostMapping
    public ResponseEntity<UserResponse> saveUser(@RequestBody User user) {
        User saved = userService.save(user);
        return ResponseEntity.ok(mapToResponse(saved));
    }
}
