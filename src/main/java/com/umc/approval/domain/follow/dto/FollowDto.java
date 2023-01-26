package com.umc.approval.domain.follow.dto;

import com.umc.approval.domain.follow.entity.Follow;
import lombok.Data;

public class FollowDto {
    @Data
    public static class FollowListResponse {
        // follow
        private Integer level;
        private String nickname;
        private String profileImage;

        // Entity -> DTO
        public FollowListResponse (Integer level, String nickname, String profileImage) {
            this.level = level;
            this.nickname = nickname;
            this.profileImage = profileImage;
        }
    }

    public static class FollowingListResponse {
        // follow
        private Integer level;
        private String nickname;
        private String profileImage;
        private Boolean isFollow;

        // Entity -> DTO
        public FollowingListResponse(Integer level, String nickname, String profileImage, Boolean isFollow) {
            this.level = level;
            this.nickname = nickname;
            this.profileImage = profileImage;
            this.isFollow = isFollow;
        }
    }
}
