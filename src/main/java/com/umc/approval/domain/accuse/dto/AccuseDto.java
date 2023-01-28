package com.umc.approval.domain.accuse.dto;

import com.umc.approval.domain.accuse.entity.Accuse;
import com.umc.approval.domain.comment.entity.Comment;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import lombok.*;

public class AccuseDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        private Long accuseUserId;
        private Long documentId;
        private Long toktokId;
        private Long reportId;
        private Long commentId;

        public Accuse toEntity(User user, User accuseUser, Document document, Toktok toktok, Report report, Comment comment){
            return Accuse.builder()
                    .user(user)
                    .accuseUser(accuseUser)
                    .document(document)
                    .toktok(toktok)
                    .report(report)
                    .comment(comment)
                    .build();
        }
    }
}
