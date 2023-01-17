package com.umc.approval.domain.vote.service;

import com.umc.approval.domain.vote.entity.VoteOption;
import com.umc.approval.domain.vote.entity.VoteOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class VoteOptionService {

    @Autowired
    private final VoteOptionRepository voteOptionRepository;

    public void createVoteOption(VoteOption option) {
        voteOptionRepository.save(option);
    }

}
