package com.hive.content.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hive.common.event.integration.PostCreatedEvent;
import com.hive.content.dto.CreatePostRequest;
import com.hive.content.dto.PostResponse;
import com.hive.content.entity.Post;
import com.hive.content.repository.PostRepository;
import com.hive.identity.service.IdentityService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ContentService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final IdentityService identityService;
    private final Timer hydrationLatencyTimer;
    private final Cache<UUID, PostResponse> postCache;

    public ContentService(PostRepository postRepository,
            ApplicationEventPublisher eventPublisher,
            IdentityService identityService,
            MeterRegistry registry) {
        this.postRepository = postRepository;
        this.eventPublisher = eventPublisher;
        this.identityService = identityService;
        this.hydrationLatencyTimer = registry.timer("content.hydration.latency");

        // L1 Cache: 5 seconds TTL, Max 10k items
        this.postCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(5))
                .maximumSize(10_000)
                .recordStats()
                .build();
    }

    @Transactional
    public PostResponse createPost(UUID userId, CreatePostRequest request) {
        Post post = Post.builder()
                .userId(userId)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .build();

        postRepository.save(post);

        // Publish Event for Feed/Notifications
        PostCreatedEvent event = new PostCreatedEvent(
                post.getId().toString(),
                post.getUserId().toString(),
                post.getCreatedAt());
        eventPublisher.publishEvent(event);
        log.info("Post created: {}", post.getId());

        String authorName = identityService.getUsername(userId);
        PostResponse response = PostResponse.from(post, authorName);

        postCache.put(post.getId(), response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByIds(List<UUID> postIds) {
        return hydrationLatencyTimer.record(() -> {
            Map<UUID, PostResponse> hits = postCache.getAllPresent(postIds);

            List<UUID> missingIds = postIds.stream()
                    .filter(id -> !hits.containsKey(id))
                    .collect(Collectors.toList());

            if (!missingIds.isEmpty()) {
                List<Post> posts = postRepository.findAllById(missingIds);
                for (Post post : posts) {
                    PostResponse resp = mapToResponse(post);
                    hits.put(post.getId(), resp);
                    postCache.put(post.getId(), resp);
                }
            }

            return postIds.stream()
                    .map(hits::get)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList());
        });
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByAuthors(List<UUID> authorIds) {
        return hydrationLatencyTimer.record(() -> postRepository.findByUserIdInOrderByCreatedAtDesc(authorIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    private PostResponse mapToResponse(Post post) {
        String authorName = identityService.getUsername(post.getUserId());
        return PostResponse.from(post, authorName);
    }
}
