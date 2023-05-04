package com.umc.approval.domain.vote.entity;

import com.umc.approval.domain.BaseTimeEntity;
import com.umc.approval.domain.toktok.dto.ToktokDto.PostToktokRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;


@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Vote extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Boolean isSingle;

    @Column(nullable = false)
    private Boolean isAnonymous;

    @Column(nullable = false)
    private Boolean isEnd;

    @OneToMany(fetch = LAZY, mappedBy = "vote")
    private List<VoteOption> voteOptions;

    @OneToMany(fetch = LAZY, mappedBy = "vote")
    private List<UserVote> userVotes;

    public void update(PostToktokRequest request) {
        this.title = request.getVoteTitle();
        this.isSingle = request.getVoteIsSingle();
        this.isAnonymous = request.getVoteIsAnonymous();
        this.isEnd = false;
    }
}
