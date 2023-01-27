package com.umc.approval.domain.profile.dto;

import com.umc.approval.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchResponse {
        private Integer userCount;
        private List<SearchListResponse> content;

        public static SearchResponse from(List<User> users) {
            return SearchResponse.builder()
                    .userCount(users.size())
                    .content(users.stream().map(SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchListResponse {
        private Long userId;
        private String nickname;
        private String profileImage;
        private Integer level;

        public static SearchListResponse fromEntity(User user) {
            return SearchListResponse.builder()
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .level(user.getLevel())
                    .build();
        }
    }
}
