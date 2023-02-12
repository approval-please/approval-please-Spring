package com.umc.approval.domain.profile.dto;

import com.umc.approval.domain.user.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class ProfileDto {
    @Data
    public static class ProfileResponse {
        private Long userId;
        private String profileImage;
        private String introduction;
        private String nickname;
        private Integer level;
        private Long promotionPoint;
        private Integer follows;
        private Integer followings;

        // Entity -> DTO
        public ProfileResponse (Long userId, String profileImage, String introduction, String nickname, Integer level, Long promotionPoint, Integer follows, Integer followings) {
            this.userId = userId;
            this.profileImage = profileImage;
            this.introduction = introduction;
            this.nickname = nickname;
            this.level = level;
            this.promotionPoint = promotionPoint;
            this.follows = follows;
            this.followings = followings;
        }
    }

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
