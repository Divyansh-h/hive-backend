package com.hive.content.service;

import com.hive.common.event.integration.PostCreatedEvent;
import com.hive.content.dto.CreatePostRequest;
import com.hive.content.dto.PostResponse;
import com.hive.content.entity.Post;
import com.hive.content.repository.PostRepository;
import com.hive.identity.service.IdentityService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final IdentityService identityService;

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
        return PostResponse.from(post, authorName);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByIds(List<UUID> postIds) {
        return postRepository.findAllById(postIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByAuthors(List<UUID> authorIds) {
        return postRepository.findByUserIdInOrderByCreatedAtDesc(authorIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PostResponse mapToResponse(Post post) {
        String authorName = identityService.getUsername(post.getUserId());
        return PostResponse.from(post, authorName);
    }
}
