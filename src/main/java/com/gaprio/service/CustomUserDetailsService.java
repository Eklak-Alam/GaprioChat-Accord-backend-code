package com.gaprio.service;

import com.gaprio.entities.User;
import com.gaprio.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("✅ CustomUserDetailsService initialized with UserRepository");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔍 loadUserByUsername called with username: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("❌ No user found with username: " + username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        System.out.println("✅ User fetched from DB: " + user.getUsername() + " | ID: " + user.getId());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        System.out.println("✅ Returning CustomUserDetails for username: " + userDetails.getUsername());

        return userDetails;
    }
}
