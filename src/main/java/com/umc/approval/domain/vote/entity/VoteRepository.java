package com.umc.approval.domain.vote.entity;

import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findById(Long voteId);

    @Modifying
    @Query(value = "update Vote set isEnd = true where vote_id = :vote_id")
    void updateState(@Param("vote_id") Long voteId);
}
