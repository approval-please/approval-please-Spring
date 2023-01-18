package com.umc.approval.domain.vote.service;

import com.umc.approval.domain.toktok.dto.ToktokDto;
import com.umc.approval.domain.vote.dto.VoteDto;
import com.umc.approval.domain.vote.entity.Vote;
import com.umc.approval.domain.vote.entity.VoteOption;
import com.umc.approval.domain.vote.entity.VoteOptionRepository;
import com.umc.approval.domain.vote.entity.VoteRepository;
import java.util.List;
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

    @Autowired
    private final VoteOptionRepository voteOptionRepository;

    public Vote createVote(ToktokDto.PostToktokRequest request) {
        Vote vote = Vote.builder()
            .title(request.getVoteTitle())
            .isSingle(request.getVoteIsSingle())
            .isAnonymous(request.getVoteIsAnonymous())
            .isEnd(false)
            .build();

        voteRepository.save(vote);
        createVoteOption(request.getVoteOption(), vote);
        return vote;
    }

    public void createVoteOption(List<String> options, Vote vote){
        //투표 선택지 저장
        for (String option : options) {
            VoteOption voteOption = VoteOption.builder()
                .vote(vote)
                .opt(option)
                .build();
            voteOptionRepository.save(voteOption);
        }
    }

}
