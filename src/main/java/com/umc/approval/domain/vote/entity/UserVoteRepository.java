package com.umc.approval.domain.vote.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    @Query("select count(distinct user_id) from UserVote where vote_id = :vote_id")
    Integer findVotePeopleCount(@Param("vote_id") Long voteId);

    @Query("select uv from UserVote uv " +
            "join fetch uv.voteOption o " +
            "join uv.user u " +
            "join uv.vote v " +
            "where u.id = :user_id " +
            "and v.id = :vote_id")
    List<UserVote> findAllByUserAndVote(@Param("user_id") Long userId, @Param("vote_id") Long voteId);


    @Query("select uv from UserVote uv where user_id = :user_id and vote_option_id = :vote_option_id")
    Optional<UserVote> findByUserAndOption(@Param("user_id") Long userId, @Param("vote_option_id") Long voteOptionId);

    void deleteByVoteId(Long voteId);

    @Modifying
    @Query("delete from UserVote uv where user_id = :user_id and vote_id = :vote_id")
    void deleteByVoteIdAndUserId(@Param("user_id") Long userId, @Param("vote_id") Long voteId);

    // 투표 참여자 목록 조회(옵션별x)
    @Query(value = "select distinct user_id from user_vote where vote_id = :vote_id", nativeQuery = true)
    List<Long> findA(@Param("vote_id") Long voteId);

    @Query(value = "select distinct user_id from user_vote where vote_option_id = :vote_option_id", nativeQuery = true)
    List<Long> findVotePeopleByOptionId(@Param("vote_option_id") Long option_id);

    @Query(value = "select uv from UserVote uv where vote_option_id = :vote_option_id")
    List<UserVote> findByOptionId(@Param("vote_option_id") Long option_id);
}