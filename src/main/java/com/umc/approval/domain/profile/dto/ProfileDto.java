package com.umc.approval.domain.profile.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ProfileDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchResponse {
        private Integer userCount;
        private List<SearchListResponse> content;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchListResponse {
        private Long userId;
        private String nickname;
        private String profileImage;
        private Integer level;
    }
}
