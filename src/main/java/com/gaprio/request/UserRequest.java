package com.gaprio.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user signup/update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    @Size(min = 6, message = "Password must be at least 6 characters", groups = OnCreate.class)
    private String password;

    private String avatarUrl;

    @Size(max = 500, message = "About cannot exceed 500 characters")
    private String about;

    // Validation groups
    public interface OnCreate {}
    public interface OnUpdate {}
}