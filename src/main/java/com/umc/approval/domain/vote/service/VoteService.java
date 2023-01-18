package com.umc.approval.domain.vote.service;

import com.umc.approval.domain.vote.entity.VoteOptionRepository;
import com.umc.approval.domain.vote.entity.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoteOptionRepository voteOptionRepository;

}
