package com.umc.approval.domain.follow.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f " +
            "join fetch f.toUser to " +
            "where f.fromUser.id = :fromUserId " +
            "and f.toUser.id in :toUserIds")
    List<Follow> findAllByToUserIn(Long fromUserId, List<Long> toUserIds);
}
