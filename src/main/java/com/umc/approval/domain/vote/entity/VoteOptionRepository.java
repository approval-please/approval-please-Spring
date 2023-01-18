package com.umc.approval.domain.vote.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {

}
