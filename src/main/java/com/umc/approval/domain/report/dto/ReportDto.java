package com.umc.approval.domain.report.dto;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.report.entity.Report;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.util.DateUtil;

import java.util.List;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.stream.Collectors;

public class ReportDto {

    @Data
    public static class ReportRequest {

        @NotNull(message = "결재서류를 선택해야합니다.")
        private Long documentId;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @Size(max = 4, message = "링크 첨부는 최대 4개까지 가능합니다")
        private List<LinkDto.Request> link;

        @Size(max = 4, message = "태그 첨부는 최대 4개까지 가능합니다")
        private List<String> tag;

        private List<String> images;

        public Report toEntity(Document document) {
            return Report.builder()
                .content(content)
                .document(document)
                .notification(true)
                .view(0L)
                .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReportGetDocumentResponse {

        private List<DocumentListResponse> content;

        public static ReportGetDocumentResponse from(List<DocumentListResponse> content) {
            return ReportGetDocumentResponse.builder()
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
        private Long userId;
        private String profileImage;
        private String nickname;
        private Integer level;

        // document
        private Long documentId;
        private String documentImageUrl;
        private Integer documentImageCount;
        private Integer documentCategory;
        private String documentTitle;
        private String documentContent;
        private List<String> documentTag;

        // report
        private Long reportId;
        private String reportContent;
        private List<String> reportImageUrl;
        private List<LinkDto.Response> reportLink;
        private List<String> reportTag;

        private Long likedCount;
        private Boolean likeOrNot;
        private Boolean followOrNot;
        private Boolean writerOrNot;
        private Boolean scrapOrNot;
        private Long scrapCount;
        private Long commentCount;
        private Boolean isNotification;
        private String datetime;
        private Boolean isModified;
        private Long view;

        public GetReportResponse(User user, Document document, Report report,
                                 List<String> documentTagList, String documentImageUrlList, Integer documentImageCount,
                                 List<String> reportTagList, List<String> reportImageUrlList,
                                 List<LinkDto.Response> reportLink, Long likedCount, Long scrapCount, Long commentCount,
                                 Boolean likeOrNot, Boolean followOrNot, Boolean isModified, Boolean writerOrNot, Boolean scrapOrNot) {
            this.userId = user.getId();
            this.profileImage = user.getProfileImage();
            this.nickname = user.getNickname();
            this.level = user.getLevel();

            this.documentId = document.getId();
            this.documentImageUrl = documentImageUrlList;
            this.documentImageCount = documentImageCount;
            this.documentContent = document.getContent();
            this.documentCategory = document.getCategory().getValue();
            this.documentTitle = document.getTitle();
            this.documentTag = documentTagList;

            this.reportContent = report.getContent();
            this.reportImageUrl = reportImageUrlList;
            this.reportLink = reportLink;
            this.reportTag = reportTagList;

            this.likedCount = likedCount;
            this.scrapCount = scrapCount;
            this.datetime = DateUtil.convert(report.getCreatedAt());
            this.commentCount = commentCount;
            this.isNotification = report.getNotification();
            this.view = report.getView();
            this.likeOrNot = likeOrNot;
            this.followOrNot = followOrNot;
            this.isModified = isModified;
            this.reportId = report.getId();
            this.writerOrNot = writerOrNot;
            this.scrapOrNot = scrapOrNot;
        }


    }

    // 게시글 목록 조회
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProfileResponse {
        private Integer reportCount;
        private List<SearchListResponse> reportContent;

        public static ProfileResponse from(List<Report> reports) {
            return ProfileResponse.builder()
                    .reportCount(reports.size())
                    .reportContent(reports.stream().map(SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }


    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchResponse {
        private Integer reportCount;
        private List<SearchListResponse> content;

        public static SearchResponse from(List<Report> reports) {
            return SearchResponse.builder()
                    .reportCount(reports.size())
                    .content(reports.stream().map(SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchListResponse {
        private Long reportId;
        private Long userId;
        private String nickname;
        private Integer userLevel;
        private String content;
        private List<String> tag;
        private List<String> images;
        private LinkDto.Response link;
        private Integer likeCount;
        private Integer commentCount;
        private Long view;
        private String datetime;
        private DocumentResponse document;

        public static SearchListResponse fromEntity(Report report) {
            return SearchListResponse.builder()
                    .reportId(report.getId())
                    .userId(report.getDocument().getUser().getId())
                    .nickname(report.getDocument().getUser().getNickname())
                    .userLevel(report.getDocument().getUser().getLevel())
                    .content(report.getContent())
                    .tag(report.getTags() == null ? null :
                            report.getTags().stream().map(Tag::getTag).collect(Collectors.toList()))
                    .images(report.getImages() == null ? null :
                            report.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                    .link((report.getLinks() == null || report.getLinks().isEmpty()) ?
                            null : LinkDto.Response.fromEntity(report.getLinks().get(0)))
                    .likeCount(report.getLikes() == null ? 0 : report.getLikes().size())
                    .commentCount(report.getComments() == null ? 0 : report.getComments().size())
                    .view(report.getView())
                    .datetime(DateUtil.convert(report.getCreatedAt()))
                    .document(DocumentResponse.fromEntity(report.getDocument()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DocumentResponse {
        private Long documentId;
        private String title;
        private String content;
        private List<String> tag;
        private String thumbnailImage;
        private Integer imageCount;

        public static DocumentResponse fromEntity(Document document) {
            return DocumentResponse.builder()
                    .documentId(document.getId())
                    .title(document.getTitle())
                    .content(document.getContent())
                    .tag(document.getTags() == null ? null :
                            document.getTags().stream().map(Tag::getTag).collect(Collectors.toList()))
                    .thumbnailImage((document.getImages() == null || document.getImages().isEmpty()) ?
                            null : document.getImages().get(0).getImageUrl())
                    .imageCount(document.getImages() == null ? 0 : document.getImages().size())
                    .build();
        }
    }
}