package com.umc.approval.domain.follow.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("select f from Follow f " +
            "join fetch f.toUser to " +
            "where f.fromUser.id = :fromUserId " +
            "and f.toUser.id in :toUserIds")
    List<Follow> findAllByToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserIds") List<Long> toUserIds);

    @Query("select f from Follow f where f.toUser.id = :userId")
    List<Follow> findMyFollowings(@Param("userId") Long userId);

    @Query("select f from Follow f " +
            "join fetch f.toUser u " +
            "where f.fromUser.id = :userId")
    List<Follow> findMyFollowers(@Param("userId") Long userId);

    @Query("select count(f) from Follow f " +
            "where f.fromUser.id = :userId")
    Integer countByFromUser(@Param("userId") Long userId);

    @Query("select count(f) from Follow f " +
            "where f.toUser.id = :userId")
    Integer countByToUser(@Param("userId") Long userId);

    @Query("select count(*) from Follow where from_user_id = :from_userId and to_user_id = :to_userId")
    Integer countFollowOrNot(@Param("from_userId") Long from_userId, @Param("to_userId") Long to_userId);

    Optional<Follow> findByFromUserIdAndToUserId(Long fromUserId, Long toUserId);
}