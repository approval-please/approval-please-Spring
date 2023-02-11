package com.umc.approval.global.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceType {

    WRITE_DOCUMENT("결재서류 작성"),
    FINAL_APPROVE_MY_DOCUMENT("결재서류 최종 결재"),
    APPROVE_OTHER_DOCUMENT("결재서류 승인/반려"),
    FINAL_APPROVE_OTHER_DOCUMENT("승인한 서류가 최종 승인"),
    FINAL_REJECT_OTHER_DOCUMENT("반려한 서류가 최종 반려"),
    WRITE_REPORT("결재보고서 작성");

    private final String content;
}
