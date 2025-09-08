package com.gaprio.service;

import com.gaprio.entities.User;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

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
    // FIXED: Return List instead of Page
    public List<User> searchByNameOrUsername(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchQuery = query.trim();
        Pageable pageable = PageRequest.of(0, limit > 0 ? limit : 20);

        System.out.println("Searching for: '" + searchQuery + "' with limit: " + limit);

        Page<User> resultPage = userRepository.searchUsers(searchQuery, pageable);
        List<User> results = resultPage.getContent();

        System.out.println("Found " + results.size() + " users");
        results.forEach(user -> System.out.println("User: " + user.getUsername()));

        return results;
    }


    /**
     * Create or update user. Note: password encoding should happen before calling this method
     * if you're creating via service (AuthService handles password encoding).
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
