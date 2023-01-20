package com.umc.approval.domain.comment.dto;

import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
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

        public Comment toEntity(
                User user, Document document, Report report,
                Toktok toktok, Comment parentComment, String imageUrl
        ) {
            return Comment.builder()
                    .user(user)
                    .document(document)
                    .report(report)
                    .toktok(toktok)
                    .parentComment(parentComment)
                    .content(content)
                    .imageUrl(imageUrl)
                    .build();
        }
    }
}
