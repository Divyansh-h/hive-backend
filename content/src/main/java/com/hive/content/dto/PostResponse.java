package com.hive.content.dto;

import com.hive.content.entity.Post;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {
    private UUID id;
    private UUID userId;
    private String authorName;
    private String content;
    private String imageUrl;
    private Instant createdAt;

    public static PostResponse from(Post post, String authorName) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .authorName(authorName)
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
