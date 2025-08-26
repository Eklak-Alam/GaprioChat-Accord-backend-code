package com.gaprio.service;

import com.gaprio.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
        System.out.println("✅ CustomUserDetails created for: " + user.getUsername() + " | Role: " + user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_" + user.getRole().name();
        System.out.println("🔑 Granted Authority: " + role);
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        System.out.println("🔒 Fetching password for user: " + user.getUsername());
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        System.out.println("👤 Fetching username: " + user.getUsername());
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        System.out.println("⏳ Account non-expired check for: " + user.getUsername());
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        System.out.println("🔓 Account non-locked check for: " + user.getUsername());
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        System.out.println("🗝️ Credentials non-expired check for: " + user.getUsername());
        return true;
    }

    @Override
    public boolean isEnabled() {
        System.out.println("✅ Account enabled check for: " + user.getUsername());
        return true;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "username='" + user.getUsername() + '\'' +
                ", role=" + user.getRole() +
                ", email='" + user.getEmail() + '\'' +
                '}';
    }
}
