package com.gaprio.service;

import com.gaprio.entities.RefreshToken;
import com.gaprio.entities.User;
import com.gaprio.exceptions.ResourceNotFoundException;
import com.gaprio.repository.RefreshTokenRepository;
import com.gaprio.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Manage refresh tokens: create, find, revoke.
 */
@Service
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public RefreshToken create(UUID userId, String tokenHash, long expiresAtMillis) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(expiresAtMillis)
                .revoked(false)
                .build();

        return refreshTokenRepository.save(token);
    }

    public RefreshToken findByHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "tokenHash", tokenHash));
    }

    public void revokeAllForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));
        refreshTokenRepository.deleteByUser(user);
    }
}
