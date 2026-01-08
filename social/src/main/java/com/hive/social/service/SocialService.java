package com.hive.social.service;

import com.hive.common.exception.HiveException;
import com.hive.social.entity.Follow;
import com.hive.social.repository.FollowRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final FollowRepository followRepository;

    @Transactional
    public void followUser(UUID followerId, UUID followeeId) {
        if (followerId.equals(followeeId)) {
            throw new HiveException("Cannot follow yourself", HttpStatus.BAD_REQUEST);
        }
        if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new HiveException("Already following", HttpStatus.CONFLICT);
        }

        Follow follow = Follow.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .build();

        followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(UUID followerId, UUID followeeId) {
        if (!followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new HiveException("Not following", HttpStatus.NOT_FOUND);
        }
        followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}
