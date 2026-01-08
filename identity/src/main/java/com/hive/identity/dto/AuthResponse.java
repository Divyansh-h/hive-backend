package com.hive.identity.dto;

import com.hive.identity.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String userId;
    private String username;

    public static AuthResponse from(User user, String token) {
        return AuthResponse.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .token(token)
                .build();
    }
}
