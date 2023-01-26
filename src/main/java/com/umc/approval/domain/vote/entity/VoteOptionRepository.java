package com.umc.approval.domain.vote.entity;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    List<VoteOption> findByVote(Vote vote);

    List<String> findOptionListByVote(Vote vote);

}
