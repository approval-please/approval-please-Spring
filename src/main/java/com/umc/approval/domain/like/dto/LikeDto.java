package com.umc.approval.domain.like.dto;

import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.user.entity.User;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

public class LikeDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        private Long documentId;
        private Long toktokId;
        private Long reportId;
        private Long commentId;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ListResponse {
        private Integer page;
        private Integer totalPage;
        private Long totalElement;
        private List<Response> content;

        public static ListResponse from(Page<Like> page, List<Response> content) {
            return ListResponse.builder()
                    .page(page.getNumber())
                    .totalPage(page.getTotalPages())
                    .totalElement(page.getTotalElements())
                    .content(content)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse {
        Boolean isLike;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private Long userId;
        private String profileImage;
        private String nickname;
        private Integer level;
        private Boolean isFollow;

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
