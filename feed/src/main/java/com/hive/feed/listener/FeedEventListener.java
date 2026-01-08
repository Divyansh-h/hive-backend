package com.hive.feed.listener;

import com.hive.common.event.integration.PostCreatedEvent;
import com.hive.feed.service.FeedService;
import com.hive.social.entity.Follow;
import com.hive.social.repository.FollowRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedEventListener {

    private final FeedService feedService;
    private final FollowRepository followRepository;

    @KafkaListener(topics = "post-created", groupId = "feed-group")
    public void handlePostCreated(PostCreatedEvent event) {
        log.info("Received PostCreatedEvent: {}", event.getPostId());

        UUID authorId = UUID.fromString(event.getUserId());
        List<Follow> followers = followRepository.findByFolloweeId(authorId);

        // Fan-out
        for (Follow follow : followers) {
            feedService.addToFeed(follow.getFollowerId(), event.getPostId());
        }

        log.info("Fan-out complete for post {} to {} followers", event.getPostId(), followers.size());
    }
}
