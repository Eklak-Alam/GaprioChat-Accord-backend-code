package com.gaprio.controller;

import com.gaprio.request.AuthRequest;
import com.gaprio.request.UserRequest;
import com.gaprio.response.AuthResponse;
import com.gaprio.response.UserResponse;
import com.gaprio.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 🔹 Register new user
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest req) {
        System.out.println("📝 Signup request for: " + req.getUsername());
        UserResponse userResponse = authService.register(req); // throws RuntimeException if fails
        System.out.println("✅ Signup successful - User ID: " + userResponse.getId());
        System.out.println("✅ User: " + userResponse.getUsername() + ", Email: " + userResponse.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    // 🔹 Authenticate and return JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        System.out.println("🔐 Login request for: " + req.getUsername());
        AuthResponse authResponse = authService.login(req); // throws RuntimeException if fails
        System.out.println("✅ Login successful - Token: " + authResponse.getToken());
        System.out.println("✅ Username: " + authResponse.getUsername() + ", Role: " + authResponse.getRole());
        return ResponseEntity.ok(authResponse);
    }
}
