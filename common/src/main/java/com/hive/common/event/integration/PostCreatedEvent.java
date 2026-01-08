package com.hive.common.event.integration;

import com.hive.common.event.DomainEvent;
import java.time.Instant;
import lombok.Getter;

@Getter
public class PostCreatedEvent extends DomainEvent {
    private final String postId;
    private final String userId;
    private final Instant createdAt;

    public PostCreatedEvent(String postId, String userId, Instant createdAt) {
        super();
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }
}
