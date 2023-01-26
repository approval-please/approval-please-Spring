package com.umc.approval.domain.vote.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {
    @Query("select opt from user_vote inner join vote_option"
        + "where user_vote.vote_option_id = vote_option.vote_option_id"
        + "and user_vote.vote_id = :vote_id")
    List<String> findUserVoteOption(@Param("vote_id") Long voteId);

    @Query("select count(distinct user_id) from user_vote where user_vote.vote_id = :vote_id")
    Integer findVotePeople(@Param("vote_id") Long voteId);

    @Query("select count(vote_option_id) from user_vote where user_vote.vote_id = :vote_id group by vote_option_id")
    List<Integer> findPeopleEachOption(@Param("vote_id") Long voteId);
}
