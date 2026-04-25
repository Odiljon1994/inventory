package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.LoginRequest;
import com.axia.inventorymanagment.dto.LoginResponse;
import com.axia.inventorymanagment.dto.RegisterRequest;
import com.axia.inventorymanagment.entity.Store;
import com.axia.inventorymanagment.entity.User;
import com.axia.inventorymanagment.repository.StoreRepository;
import com.axia.inventorymanagment.repository.UserRepository;
import com.axia.inventorymanagment.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private static final Set<String> VALID_ROLES = Set.of("admin", "staff", "member");

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse authenticate(LoginRequest request) {
        String identifier = request.getEmail() != null ? request.getEmail() : request.getUsername();
        log.info("Attempting login for user: {}", identifier);

        if (identifier == null || identifier.isBlank()) {
            throw new BadCredentialsException("Username or email is required");
        }

        // Try to find user by email first, then by username
        Optional<User> userOpt = Optional.empty();
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            userOpt = userRepository.findByEmail(request.getEmail());
        }
        if (userOpt.isEmpty() && request.getUsername() != null && !request.getUsername().isBlank()) {
            userOpt = userRepository.findByUsername(request.getUsername());
        }

        User user = userOpt.orElseThrow(() -> {
            log.warn("User not found: {}", identifier);
            return new BadCredentialsException("Invalid credentials");
        });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", identifier);
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            log.warn("Inactive user attempted login: {}", identifier);
            throw new BadCredentialsException("Account is inactive");
        }

        // Update last login timestamp
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        Integer storeId = user.getStore() != null ? user.getStore().getStoreId() : null;

        log.info("Login successful for user: {}, role: {}", user.getUsername(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .fullName(user.getFullName())
                .storeId(storeId)
                .build();
    }

    public LoginResponse register(RegisterRequest request) {
        log.info("Attempting registration for user: {}", request.getUsername());

        // Validate username uniqueness
        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Validate email uniqueness
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate role
        if (!VALID_ROLES.contains(request.getRole())) {
            throw new IllegalArgumentException("Role must be one of: admin, staff, member");
        }

        // Staff role requires store assignment
        if (request.getRole().equals("staff") && request.getStoreId() == null) {
            throw new IllegalArgumentException("Store ID is required for staff role");
        }

        // Admin cannot be assigned to a store
        if (request.getRole().equals("admin") && request.getStoreId() != null) {
            throw new IllegalArgumentException("Admin users cannot be assigned to a store");
        }

        Store store = null;
        if (request.getStoreId() != null) {
            store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("Store not found"));
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .store(store)
                .isActive(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}, role: {}", user.getUsername(), user.getRole());

        String token = jwtUtil.generateToken(user);
        Integer storeId = user.getStore() != null ? user.getStore().getStoreId() : null;

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .fullName(user.getFullName())
                .storeId(storeId)
                .build();
    }
}
