package com.umc.approval.domain.vote.entity;

import com.umc.approval.domain.toktok.entity.Toktok;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
