package com.hive.feed.service;

import com.hive.content.dto.PostResponse;
import com.hive.content.service.ContentService;
import com.hive.social.entity.Follow;
import com.hive.social.repository.FollowRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ContentService contentService; // Access via Service Interface
    private final FollowRepository followRepository;

    private static final String FEED_KEY_PREFIX = "user:feed:";
    private static final int MAX_FEED_SIZE = 100;

    public void addToFeed(UUID userId, String postId) {
        String key = FEED_KEY_PREFIX + userId;
        try {
            ListOperations<String, String> listOps = redisTemplate.opsForList();
            listOps.leftPush(key, postId);
            listOps.trim(key, 0, MAX_FEED_SIZE - 1);
            redisTemplate.expire(key, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("Failed to push to Redis feed for user: {}", userId, e);
            // Fallback: Do nothing on write failure? Or maybe write to a "recover" queue?
            // For Phase 1.5, we accept write failure in cache as "Eventual Consistency" via
            // fallback read.
        }
    }

    public List<PostResponse> getFeed(UUID userId, int page, int size) {
        String key = FEED_KEY_PREFIX + userId;
        long start = (long) page * size;
        long end = start + size - 1;

        try {
            List<String> feedPostIds = redisTemplate.opsForList().range(key, start, end);
            if (feedPostIds != null && !feedPostIds.isEmpty()) {
                List<UUID> postUuids = feedPostIds.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toList());
                // Hydrate via ContentService
                return contentService.getPostsByIds(postUuids);
            }
        } catch (Exception e) {
            log.warn("Redis Unavailable. Falling back to DB for user: {}", userId);
        }

        return getFeedFromFallback(userId, size);
    }

    private List<PostResponse> getFeedFromFallback(UUID userId, int limit) {
        // 1. Get who the user follows
        List<Follow> follows = followRepository.findByFollowerId(userId);
        if (follows.isEmpty()) {
            return Collections.emptyList();
        }

        List<UUID> followeeIds = follows.stream()
                .map(Follow::getFolloweeId)
                .collect(Collectors.toList());

        // 2. Get posts from these users via ContentService (Service-to-Service call)
        // This respects the module boundary.
        return contentService.getPostsByAuthors(followeeIds).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}
