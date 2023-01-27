package com.umc.approval.domain.scrap.dto;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.scrap.entity.Scrap;
import com.umc.approval.domain.toktok.entity.Toktok;
import com.umc.approval.domain.user.entity.User;
import lombok.*;

public class ScrapDto {

    @Getter
    @Builder
    @AllArgsConstructor(access =  AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        private Long documentId;
        private Long toktokId;
        private Long reportId;

        public Scrap toEntity(User user, Document document, Toktok toktok, Report report) {
            return Scrap.builder()
                    .user(user)
                    .document(document)
                    .toktok(toktok)
                    .report(report)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse {
        Boolean isScrap;
    }
}
