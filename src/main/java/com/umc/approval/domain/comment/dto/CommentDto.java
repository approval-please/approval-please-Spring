package com.umc.approval.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class CommentDto {

    @Getter
    @AllArgsConstructor
    public static class CreateRequest {
        private Long documentId;
        private Long reportId;
        private Long toktokId;
        private Long parentCommentId;
        private String content;
    }
}
