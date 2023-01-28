package com.umc.approval.domain.follow.dto;

import com.umc.approval.domain.follow.entity.Follow;
import com.umc.approval.domain.user.entity.User;
import lombok.*;

public class FollowDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        private Long toUserId;

        public Follow toEntity(User fromUser, User toUser) {
            return Follow.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse {
        Boolean isFollow;
    }

    @Data
    public static class FollowListResponse {
        // follow
        private Long userId;
        private Integer level;
        private String nickname;
        private String profileImage;

        // Entity -> DTO
        public FollowListResponse (Long id, Integer level, String nickname, String profileImage) {
            this.userId = id;
            this.level = level;
            this.nickname = nickname;
            this.profileImage = profileImage;
        }
    }

    @Data
    public static class FollowingListResponse {
        // follow
        private Long userId;
        private Integer level;
        private String nickname;
        private String profileImage;
        private Boolean isFollow;

        // Entity -> DTO
        public FollowingListResponse(Long id, Integer level, String nickname, String profileImage, Boolean isFollow) {
            this.userId = id;
            this.level = level;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.isFollow = isFollow;
        }
    }
}