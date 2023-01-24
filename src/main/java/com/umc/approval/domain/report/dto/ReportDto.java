package com.umc.approval.domain.report.dto;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.dto.LikeDto.Response;
import com.umc.approval.domain.like.entity.Like;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.util.DateUtil;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class ReportDto {

    @Data
    public static class ReportRequest {

        @NotNull(message = "결재서류를 선택해야합니다.")
        private Long documentId;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @Size(max = 4, message = "링크 첨부는 최대 4개까지 가능합니다")
        private List<String> linkUrl;

        @Size(max = 4, message = "태그 첨부는 최대 4개까지 가능합니다")
        private List<String> tag;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReportGetDocumentResponse {

        private Integer page;
        private Integer totalPage;
        private Long totalElement;
        private List<DocumentListResponse> content;

        public static ReportGetDocumentResponse from(Page<Document> page,
            List<DocumentListResponse> content) {
            return ReportGetDocumentResponse.builder()
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
    public static class DocumentListResponse {

        private Long documentId;
        private String title;
        private Integer state;
        private String datetime;

        public static DocumentListResponse fromEntity(Document document) {
            return DocumentListResponse.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .state(document.getState())
                .datetime(DateUtil.convert(document.getCreatedAt()))
                .build();
        }
    }

    // 게시글 상세 조회
    @Data
    public static class GetReportResponse {

        // user
        private String profileImage;
        private String nickname;
        private Integer level;

        // document
        private Long documentId;
        private List<String> documentImageUrl;
        private Integer documentCategory;
        private String documentTitle;
        private String documentContent;
        private List<String> documentTag;

        // report
        private String reportContent;
        private List<String> reportImageUrl;
        private List<String> reportLinkUrl;
        private List<String> reportTag;

        private Long likedCount;
        private Long scrapCount;
        private Long commentCount;
        private String datetime;
        private Long view;

        public GetReportResponse(User user, Document document, Report report,
            List<String> documentTagList, List<String> documentImageUrlList,
            List<String> reportTagList, List<String> reportImageUrlList,
            List<String> reportLinkList, Long likedCount, Long scrapCount, Long commentCount) {
            this.profileImage = user.getProfileImage();
            this.nickname = user.getNickname();
            this.level = user.getLevel();

            this.documentId = document.getId();
            this.documentImageUrl = documentImageUrlList;
            this.documentContent = document.getContent();
            this.documentCategory = document.getCategory().getValue();
            this.documentTitle = document.getTitle();
            this.documentTag = documentTagList;

            this.reportContent = report.getContent();
            this.reportImageUrl = reportImageUrlList;
            this.reportLinkUrl = reportLinkList;
            this.reportTag = reportTagList;

            this.likedCount = likedCount;
            this.scrapCount = scrapCount;
            this.datetime = DateUtil.convert(report.getCreatedAt());
            this.commentCount = commentCount;
            this.view = report.getView();
        }


    }

}
