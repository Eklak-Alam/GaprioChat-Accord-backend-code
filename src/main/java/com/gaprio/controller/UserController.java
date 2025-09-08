package com.gaprio.controller;

import com.gaprio.entities.User;
import com.gaprio.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
     * Get a user by UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get a user by username.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Search users by query (username, name, or email).
     * Example: /api/users/search?q=john&limit=10
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
            @RequestParam(name = "q") String query, // Changed to required=true
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of()); // Return empty list for empty query
        }

        List<User> results = userService.searchByNameOrUsername(query.trim(), limit);
        return ResponseEntity.ok(results);
    }

    /**
     * Create or update a user.
     * Note: Password encoding should be handled before saving.
     */
    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User saved = userService.save(user);
        return ResponseEntity.ok(saved);
    }
}
