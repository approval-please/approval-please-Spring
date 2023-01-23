package com.umc.approval.domain.like.dto;

import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.user.entity.User;
import lombok.*;

import java.util.List;

public class LikeDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ListRequest {
        Long documentId;
        Long toktokId;
        Long reportId;
    }

    @Getter
    @AllArgsConstructor
    public static class ListResponse {
        List<Response> likedPeople;
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        Long userId;
        String profileImage;
        String nickname;
        Integer level;
        Boolean isFollow;

        public static Response fromEntity(Like like, Boolean isFollow) {
            User user = like.getUser();
            return Response.builder()
                    .userId(user.getId())
                    .profileImage(user.getProfileImage())
                    .nickname(user.getNickname())
                    .level(user.getLevel())
                    .isFollow(isFollow)
                    .build();
        }
    }
}
