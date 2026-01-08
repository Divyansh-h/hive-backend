package com.hive.identity.service;

import com.hive.common.exception.HiveException;
import com.hive.identity.dto.AuthResponse;
import com.hive.identity.dto.RegisterRequest;
import com.hive.identity.entity.User;
import com.hive.identity.event.UserCreatedEvent;
import com.hive.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new HiveException("Username already exists", HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new HiveException("Email already exists", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Publish Event
        UserCreatedEvent event = new UserCreatedEvent(user.getId().toString(), user.getUsername(), user.getEmail());
        eventPublisher.publishEvent(event);
        log.info("User registered: {}", user.getId());

        // Generate Token (Dummy for now)
        String token = "dummy-jwt-token-" + user.getId();

        return AuthResponse.from(user, token);
    }
}
