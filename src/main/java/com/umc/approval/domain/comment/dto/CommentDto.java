package com.umc.approval.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class CommentDto {

    @Getter
    @AllArgsConstructor
    public static class CreateRequest {
        private Long documentId;
        private Long reportId;
        private Long toktokId;
        private Long parentCommentId;
        @NotBlank(message = "댓글의 내용은 필수 값입니다.")
        private String content;
    }
}
