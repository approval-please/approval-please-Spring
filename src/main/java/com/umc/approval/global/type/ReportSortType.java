package com.umc.approval.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportSortType {

    NEW("최신", null),
    POPULAR("인기", 0),
    FOLLOW("팔로우", 1),
    MY("내 글", 2);

    private final String sortBy;
    private final Integer value;
}
