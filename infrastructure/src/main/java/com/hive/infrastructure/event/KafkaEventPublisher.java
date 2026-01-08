package com.hive.infrastructure.event;

import com.hive.common.event.integration.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @EventListener
    public void handlePostCreatedEvent(PostCreatedEvent event) {
        log.info("Bridging PostCreatedEvent to Kafka: {}", event.getPostId());
        // In a real app, we'd wrap this in an envelope
        kafkaTemplate.send("post-created", event);
    }
}
