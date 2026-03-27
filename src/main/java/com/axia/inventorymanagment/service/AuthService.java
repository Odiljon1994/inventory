package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.LoginRequest;
import com.axia.inventorymanagment.dto.LoginResponse;
import com.axia.inventorymanagment.entity.User;
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
}
