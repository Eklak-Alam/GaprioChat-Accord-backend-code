package com.gaprio.service;


import com.gaprio.entities.Role;
import com.gaprio.entities.User;
import com.gaprio.repository.UserRepository;
import com.gaprio.request.UserRequest;
import com.gaprio.request.AuthRequest;
import com.gaprio.response.UserResponse;
import com.gaprio.response.AuthResponse;
import com.gaprio.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    // 🔹 Register new user (using UserRequest)
    public UserResponse register(UserRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(req.getName())
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(Role.USER) // default role
                .build();

        User savedUser = userRepo.save(user);

        // inside AuthService.register(...)
        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                null, // avatarUrl
                null, // about
                null  // lastSeenAt
        );

    }

    // 🔹 Authenticate and return JWT (using AuthRequest)
    public AuthResponse login(AuthRequest req) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );

            Optional<User> userOptional = userRepo.findByUsername(req.getUsername());
            if (userOptional.isEmpty()) {
                throw new RuntimeException("User not found");
            }

            User user = userOptional.get();
            String token = jwtUtil.generateToken(user.getUsername());

            return new AuthResponse(
                    user.getId(),
                    token,
                    new Date(System.currentTimeMillis() + jwtUtil.getJwtExpiration()),
                    user.getUsername(),
                    user.getRole()
            );

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}