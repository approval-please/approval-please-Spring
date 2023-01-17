package com.umc.approval.domain.vote.service;

import com.umc.approval.domain.vote.dto.VoteDto;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.domain.vote.entity.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class VoteService {
    @Autowired
    private final VoteRepository voteRepository;

    public void createVote(Vote vote) {
        voteRepository.save(vote);

    }

}
