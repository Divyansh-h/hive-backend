package com.hive.content.controller;

import com.hive.common.dto.ApiResponse;
import com.hive.content.dto.CreatePostRequest;
import com.hive.content.dto.PostResponse;
import com.hive.content.service.ContentService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// In a real scenario, we would get the UserId from the SecurityContext
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/content/posts")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestHeader("X-User-Id") String userId, // Temporary: Trust header until JWT parsing extracts it
            @Valid @RequestBody CreatePostRequest request) {

        UUID userUuid = UUID.fromString(userId);
        PostResponse response = contentService.createPost(userUuid, request);
        return new ResponseEntity<>(ApiResponse.success(response, "Post created successfully"), HttpStatus.CREATED);
    }
}
