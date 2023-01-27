package com.umc.approval.domain.performance.dto;

import com.umc.approval.domain.performance.entity.Performance;
import com.umc.approval.global.util.DateUtil;
import lombok.Data;

public class PerformanceDto {
    @Data
    public static class PerformanceListResponse {
        // performance
        private String content;
        private Integer point;
        private String datetime;

        // Entity -> DTO
        public PerformanceListResponse(Performance performance, String content, Integer point) {
            this.content = content;
            this.point = point;
            this.datetime = DateUtil.convert(performance.getCreatedAt());
        }
    }
}
