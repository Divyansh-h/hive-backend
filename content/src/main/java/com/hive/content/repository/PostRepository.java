package com.hive.content.repository;

import com.hive.content.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Post> findByUserIdInOrderByCreatedAtDesc(List<UUID> userIds);
}
