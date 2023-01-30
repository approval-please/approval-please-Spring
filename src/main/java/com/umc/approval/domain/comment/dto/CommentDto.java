package com.umc.approval.domain.comment.dto;

import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.util.DateUtil;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

public class CommentDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class CreateRequest {
        private Long documentId;
        private Long reportId;
        private Long toktokId;
        private Long parentCommentId;
        @NotBlank(message = "댓글의 내용은 필수 값입니다.")
        private String content;
        private String image;

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
        private Integer commentCount;
        private List<ParentResponse> content;

        public static ListResponse from(List<Comment> comments, Long userId, Long writerId, List<Like> likes) {
            List<ParentResponse> content = comments.stream()
                    .map(c -> ParentResponse.from(c, userId, writerId, likes))
                    .collect(Collectors.toList());
            return ListResponse.builder()
                    .commentCount(comments.size())
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
        private String imageUrl;
        private List<ChildResponse> childComment;
        private Boolean isWriter;
        private Boolean isMy;
        private Boolean isLike;
        private Boolean isDeleted;
        private Boolean isModified;
        private Integer likeCount;
        private String datetime;

        public static ParentResponse from(Comment comment, Long userId, Long writerId, List<Like> likes) {
            List<ChildResponse> childComment = comment.getChildComment().stream()
                    .map(c -> ChildResponse.from(c, userId, writerId, likes))
                    .collect(Collectors.toList());
            return ParentResponse.builder()
                    .commentId(comment.getId())
                    .userId(comment.getUser().getId())
                    .profileImage(comment.getUser().getProfileImage())
                    .nickname(comment.getUser().getNickname())
                    .level(comment.getUser().getLevel())
                    .content(comment.getContent())
                    .imageUrl(comment.getImageUrl())
                    .childComment(childComment)
                    .isWriter(writerId.equals(comment.getUser().getId()))
                    .isMy(userId != null && userId.equals(comment.getUser().getId()))
                    .isLike(likes.stream().anyMatch(l -> l.getComment().getId() == comment.getId()))
                    .isDeleted(comment.getIsDeleted())
                    .isModified(!comment.getCreatedAt().isEqual(comment.getModifiedAt()))
                    .likeCount(comment.getLikes().size())
                    .datetime(DateUtil.convert(comment.getCreatedAt()))
                    .build();
        }
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
        private String imageUrl;
        private Boolean isWriter;
        private Boolean isMy;
        private Boolean isLike;
        private Boolean isModified;
        private Integer likeCount;
        private String datetime;

        public static ChildResponse from(Comment comment, Long userId, Long writerId, List<Like> likes) {
            return ChildResponse.builder()
                    .commentId(comment.getId())
                    .userId(comment.getUser().getId())
                    .profileImage(comment.getUser().getProfileImage())
                    .nickname(comment.getUser().getNickname())
                    .level(comment.getUser().getLevel())
                    .content(comment.getContent())
                    .imageUrl(comment.getImageUrl())
                    .isWriter(writerId.equals(comment.getUser().getId()))
                    .isMy(userId != null && userId.equals(comment.getUser().getId()))
                    .isLike(likes.stream().anyMatch(l -> l.getComment().getId() == comment.getId()))
                    .isModified(!comment.getCreatedAt().isEqual(comment.getModifiedAt()))
                    .likeCount(comment.getLikes().size())
                    .datetime(DateUtil.convert(comment.getCreatedAt()))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {
        @NotBlank(message = "댓글의 내용은 필수 값입니다.")
        private String content;
        private String image;
    }
}
