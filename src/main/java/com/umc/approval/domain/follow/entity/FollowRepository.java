package com.umc.approval.domain.follow.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.data.repository.query.Param;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f " +
            "join fetch f.toUser to " +
            "where f.fromUser.id = :fromUserId " +
            "and f.toUser.id in :toUserIds")
    List<Follow> findAllByToUserIn(Long fromUserId, List<Long> toUserIds);

    @Query("select count(f) from Follow f " +
            "where f.fromUser.id = :userId")
    Integer countByFromUser(Long userId);

    @Query("select count(f) from Follow f " +
            "where f.toUser.id = :userId")
    Integer countByToUser(Long userId);

    @Query("select count(*) from Follow where from_user_id = :from_userId and to_user_id = :to_userId")
    Integer countFollowOrNot(@Param("from_userId") Long from_userId, @Param("to_userId") Long to_userId);
}
