package com.gaprio.service;

import com.gaprio.entities.User;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Basic user-related business logic.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find user by UUID, or throw ResourceNotFoundException.
     */
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
    }

    /**
     * Find user by username, or throw ResourceNotFoundException.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Search users by username or name (case-insensitive). Simple implementation.
     * For large datasets, create a custom DB query with ILIKE/Gin index.
     */
    public List<User> searchByNameOrUsername(String q, int limit) {
        if (q == null || q.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String lower = q.trim().toLowerCase();
        return userRepository.findAll()
                .stream()
                .filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(lower))
                        || (u.getName() != null && u.getName().toLowerCase().contains(lower))
                        || (u.getEmail() != null && u.getEmail().toLowerCase().contains(lower)))
                .limit(limit > 0 ? limit : 20)
                .collect(Collectors.toList());
    }

    /**
     * Create or update user. Note: password encoding should happen before calling this method
     * if you're creating via service (AuthService handles password encoding).
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
