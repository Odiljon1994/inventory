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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new BadCredentialsException("Invalid username or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!user.getIsActive()) {
            log.warn("Inactive user attempted login: {}", request.getUsername());
            throw new BadCredentialsException("Account is inactive");
        }

        String token = jwtUtil.generateToken(user);
        Integer storeId = user.getStore() != null ? user.getStore().getStoreId() : null;

        log.info("Login successful for user: {}, role: {}", request.getUsername(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .role(user.getRole())
                .fullName(user.getFullName())
                .storeId(storeId)
                .build();
    }

    public LoginResponse register(RegisterRequest request) {
        log.info("Attempting registration for user: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        if (!request.getRole().equals("ADMIN") && !request.getRole().equals("STORE")) {
            throw new IllegalArgumentException("Role must be ADMIN or STORE");
        }

        if (request.getRole().equals("STORE") && request.getStoreId() == null) {
            throw new IllegalArgumentException("Store ID is required for STORE role");
        }

        if (request.getRole().equals("ADMIN") && request.getStoreId() != null) {
            throw new IllegalArgumentException("Admin users cannot be assigned to a store");
        }

        Store store = null;
        if (request.getStoreId() != null) {
            store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new IllegalArgumentException("Store not found"));
        }

        User user = User.builder()
                .username(request.getUsername())
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
