package com.umc.approval.domain.follow.dto;

import com.umc.approval.domain.follow.entity.Follow;
import lombok.Data;

public class FollowDto {
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
