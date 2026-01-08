package com.hive.feed.controller;

import com.hive.common.dto.ApiResponse;
import com.hive.content.dto.PostResponse;
import com.hive.feed.service.FeedService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getFeed(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        UUID userUuid = UUID.fromString(userId);
        List<PostResponse> feed = feedService.getFeed(userUuid, page, size);
        return ResponseEntity.ok(ApiResponse.success(feed));
    }
}
