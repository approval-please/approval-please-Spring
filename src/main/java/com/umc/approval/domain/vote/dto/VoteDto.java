package com.umc.approval.domain.vote.dto;

import com.umc.approval.domain.vote.entity.Vote;
import javax.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteDto {
    private String title;
    private Boolean isSingle;
    private Boolean isAnonymous;
    private Boolean isEnd;

}
