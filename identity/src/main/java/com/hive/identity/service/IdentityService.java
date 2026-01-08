package com.hive.identity.service;

import com.hive.identity.entity.User;
import com.hive.identity.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public String getUsername(UUID userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown User");
    }
}
