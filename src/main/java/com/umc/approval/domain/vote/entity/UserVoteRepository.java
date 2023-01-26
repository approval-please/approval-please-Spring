package com.umc.approval.domain.vote.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {

    @Query("select count(distinct user_id) from UserVote where vote_id = :vote_id")
    Integer findVotePeople(@Param("vote_id") Long voteId);

    @Query(value = "select count(*) from user_vote where user_vote.vote_id = :vote_id group by user_vote.vote_option_id", nativeQuery = true)
    List<Integer> findPeopleEachOption(@Param("vote_id") Long voteId);

//    @Query("select u.vote_option_id from UserVote u "
//            + "join VoteOption v on u.vote_option_id = v.vote_option_id" + " where vote_id = :vote_id and user_id = :user_id")
//    List<String> findUserVoteOption(@Param("vote_id") Long voteId, @Param("user_id") Long userId);
}
