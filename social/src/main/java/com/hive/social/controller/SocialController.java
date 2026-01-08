package com.hive.social.controller;

import com.hive.common.dto.ApiResponse;
import com.hive.social.service.SocialService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
public class SocialController {

    private final SocialService socialService;

    @PostMapping("/follow/{followeeId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID followeeId) {

        UUID followerUuid = UUID.fromString(userId);
        socialService.followUser(followerUuid, followeeId);
        return new ResponseEntity<>(ApiResponse.success(null, "Followed successfully"), HttpStatus.OK);
    }

    @DeleteMapping("/follow/{followeeId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID followeeId) {

        UUID followerUuid = UUID.fromString(userId);
        socialService.unfollowUser(followerUuid, followeeId);
        return new ResponseEntity<>(ApiResponse.success(null, "Unfollowed successfully"), HttpStatus.OK);
    }
}
