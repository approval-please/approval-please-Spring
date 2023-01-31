package com.umc.approval.domain.performance.dto;

import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.document.dto.DocumentDto;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.global.util.DateUtil;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class PerformanceDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchListResponse {
        // performance
        private Long performanceId;
        private String content;
        private Integer point;
        private String datetime;

        public static PerformanceDto.SearchListResponse fromEntity(Performance performance) {
            return PerformanceDto.SearchListResponse.builder()
                    .performanceId(performance.getId())
                    .content(performance.getContent())
                    .point(performance.getPoint())
                    .datetime(DateUtil.convert(performance.getCreatedAt()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchResponse {
        private Integer performanceCount;
        private List<PerformanceDto.SearchListResponse> content;

        public static PerformanceDto.SearchResponse from(List<Performance> performances) {
            return PerformanceDto.SearchResponse.builder()
                    .performanceCount(performances.size())
                    .content(performances.stream().map(PerformanceDto.SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }
}
