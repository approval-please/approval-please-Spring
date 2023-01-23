package com.umc.approval.domain.comment.dto;

import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import lombok.*;
import org.springframework.data.domain.Page;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class CommentDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
        private Long documentId;
        private Long toktokId;
        private Long reportId;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
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
                    .isDeleted(false)
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ListResponse {
        private Integer page;
        private Integer totalPage;
        private Long totalElement;
        private List<ParentResponse> content;

        public static LikeDto.ListResponse from(Page<Like> page, List<LikeDto.Response> content) {
            return LikeDto.ListResponse.builder()
                    .page(page.getNumber())
                    .totalPage(page.getTotalPages())
                    .totalElement(page.getTotalElements())
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ParentResponse {
        private Long commentId;
        private Long userId;
        private String profileImage;
        private String nickname;
        private Integer level;
        private String content;
        private List<ChildResponse> childComment;
        private Boolean isWriter;
        private Boolean isMy;
        private Boolean isLike;
        private Boolean isDeleted;
        private Integer likeCount;
        private String datetime;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ChildResponse {
        private Long commentId;
        private Long userId;
        private String profileImage;
        private String nickname;
        private Integer level;
        private String content;
        private Boolean isWriter;
        private Boolean isMy;
        private Boolean isLike;
        private Integer likeCount;
        private String datetime;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "댓글의 내용은 필수 값입니다.")
        private String content;
    }
}
