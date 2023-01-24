package com.umc.approval.domain.follow.entity;

import com.umc.approval.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f " +
            "join fetch f.toUser to " +
            "where f.fromUser.id = :fromUserId " +
            "and f.toUser.id in :toUserIds")
    List<Follow> findAllByToUserId(Long fromUserId, List<Long> toUserIds);

    @Query("select f from Follow f where f.toUser.id = :userId")
    List<Follow> findMyFollowers(Long userId);

    @Query("select f from Follow f where f.fromUser.id = :userId")
    List<Follow> findMyFollowing(Long userId);

    @Query("select count(f) from Follow f " +
            "where f.fromUser.id = :userId")
    Integer countByFromUser(Long userId);

    @Query("select count(f) from Follow f " +
            "where f.toUser.id = :userId")
    Integer countByToUser(Long userId);
}
