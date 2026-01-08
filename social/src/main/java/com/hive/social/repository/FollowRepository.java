package com.hive.social.repository;

import com.hive.social.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FollowRepository extends JpaRepository<Follow, UUID> {
    List<Follow> findByFolloweeId(UUID followeeId);

    List<Follow> findByFollowerId(UUID followerId);

    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    void deleteByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);
}
