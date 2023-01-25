package com.umc.approval.domain.vote.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVoteRepository extends JpaRepository<UserVote, Long> {
    void deleteByVoteId(Long voteId);
}
