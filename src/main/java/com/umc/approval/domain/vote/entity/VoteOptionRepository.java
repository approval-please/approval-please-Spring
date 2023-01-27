package com.umc.approval.domain.vote.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByVote(Vote vote);

    @Query("select opt from VoteOption where vote_id = :vote_id")
    List<String> findOptionListByVote(@Param("vote_id") Long voteId);

    Optional<VoteOption> findById(Long id);


}
