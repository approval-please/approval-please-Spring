package com.umc.approval.domain.document.dto;

import com.umc.approval.domain.approval.entity.Approval;
import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.image.entity.Image;
import com.umc.approval.domain.link.dto.LinkDto;
import com.umc.approval.domain.link.entity.Link;
import com.umc.approval.domain.tag.entity.Tag;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.type.CategoryType;
import com.umc.approval.global.util.DateUtil;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentDto {

    @Data
    public static class DocumentRequest {
        @NotNull(message = "게시글의 카테고리는 필수 값입니다.")
        @Min(value = 0, message = "카테고리는 0부터 17까지의 정수 값입니다.")
        @Max(value = 17, message = "카테고리는 0부터 17까지의 정수 값입니다.")
        private Integer category;

        @NotBlank(message = "게시글의 제목은 필수 값입니다.")
        private String title;

        @NotBlank(message = "게시글의 내용은 필수 값입니다.")
        private String content;

        @Size(max = 4, message = "태그는 최대 4개까지 첨부 가능합니다.")
        private List<String> tag;

        @Size(max = 4, message = "이미지는 최대 4개까지 첨부 가능합니다.")
        private List<String> images;

        private @Valid LinkDto.Request link;

        // DTO -> Entity
        public Document toEntity(User user, CategoryType categoryType) {
            return Document.builder()
                    .user(user)
                    .category(categoryType)
                    .title(title)
                    .content(content)
                    .state(2) //승인대기중
                    .view(0L)
                    .notification(true)
                    .build();
        }
    }

    // 결재서류 상세 조회
    @Data
    public static class GetDocumentResponse {
        private Long documentId;
        private Long userId;
        private Integer category;

        // user
        private String profileImage;
        private String nickname;
        private Integer level;
        private Boolean isWriter;
        private Boolean reportMade;
        private Long reportId;

        // document
        private String title;
        private String content;
        private List<String> tag;
        private List<String> imageUrl;
        private LinkDto.Response link;

        // state
        private Integer state;
        private Integer approveCount;
        private Integer rejectCount;

        // etc..
        private Integer likedCount;
        private Integer commentCount;
        private String datetime;
        private Boolean isModified;
        private Long view;

        private Boolean isLiked;
        private Boolean isScrap;
        private Integer isVoted;
        private Boolean isNotification;

        // Entity -> DTO
        public GetDocumentResponse(Document document, User user,
                                   List<String> tagNameList, List<String> imageUrlList,
                                   Link link, int approveCount, int rejectCount,
                                   int likedCount, int commentCount,
                                   boolean isModified, boolean likeOrNot, boolean scrapOrNot,
                                   Boolean isWriter, boolean reportMade, Long reportId, int isVoted) {
            this.userId = user.getId();
            this.profileImage = user.getProfileImage();
            this.nickname = user.getNickname();
            this.level = user.getLevel();
            this.isWriter = isWriter;
            this.reportMade = reportMade;
            this.reportId = reportId;

            this.documentId = document.getId();
            this.category = document.getCategory().getValue();
            this.title = document.getTitle();
            this.content = document.getContent();
            this.link = link == null ? null : LinkDto.Response.fromEntity(link);
            this.tag = tagNameList;
            this.imageUrl = imageUrlList;

            this.state = document.getState();
            this.approveCount = approveCount;
            this.rejectCount = rejectCount;

            this.likedCount = likedCount;
            this.commentCount = commentCount;
            this.datetime = DateUtil.convert(document.getCreatedAt());
            this.isModified = isModified;
            this.view = document.getView();

            this.isLiked = likeOrNot;
            this.isScrap = scrapOrNot;
            this.isVoted = isVoted;
            this.isNotification = document.getNotification();
        }
    }

    @Data
    public static class GetDocumentListResponse {
        private List<DocumentDto.DocumentListResponse> content;

        public GetDocumentListResponse(List<DocumentDto.DocumentListResponse> content) {
            this.content = content;
        }
    }

    // 결재서류 목록 조회
    @Data
    public static class DocumentListResponse {
        // document
        private Long documentId;
        private Integer category;
        private String title;
        private String content;
        private List<String> tag;
        private String image;
        private Integer imageCount;

        // state
        private Integer state;
        private Integer approveCount;
        private Integer rejectCount;

        // etc..
        private String datetime;
        private Long view;

        // Entity -> DTO
        public DocumentListResponse(Document document, List<Tag> tagNameList, List<Image> imageList, List<Approval> approvalList) {
            this.documentId = document.getId();
            this.category = document.getCategory().getValue();
            this.title = document.getTitle();
            this.content = document.getContent();
            this.tag = tagNameList.stream().map(Tag::getTag).collect(Collectors.toList());
            this.image = imageList.size() == 0 ? null : imageList.get(0).getImageUrl(); // 첫번째 이미지 url
            this.imageCount = imageList.size(); // 이미지 개수

            this.state = document.getState();
            this.approveCount = (int) approvalList.stream().filter(Approval::getIsApprove).count();
            this.rejectCount = (int) approvalList.stream().filter(a -> !a.getIsApprove()).count();

            this.datetime = DateUtil.convert(document.getCreatedAt());
            this.view = document.getView();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchResponse {
        private Integer documentCount;
        private List<SearchListResponse> content;

        public static SearchResponse from(List<Document> documents) {
            return SearchResponse.builder()
                    .documentCount(documents.size())
                    .content(documents.stream().map(SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProfileResponse {
        private Integer documentCount;
        private List<SearchListResponse> documentContent;

        public static ProfileResponse from(List<Document> documents) {
            return ProfileResponse.builder()
                    .documentCount(documents.size())
                    .documentContent(documents.stream().map(SearchListResponse::fromEntity).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SearchListResponse {
        private Long documentId;
        private Integer category;
        private Integer state;
        private String title;
        private String content;
        private List<String> tag;
        private LinkDto.Response link;
        private String thumbnailImage;
        private Integer imageCount;
        private Long view;
        private Integer approvalCount;
        private Integer rejectCount;
        private String datetime;

        public static SearchListResponse fromEntity(Document document) {
            return SearchListResponse.builder()
                    .documentId(document.getId())
                    .category(document.getCategory().getValue())
                    .state(document.getState())
                    .title(document.getTitle())
                    .content(document.getContent())
                    .tag(document.getTags() == null ? null : document.getTags().stream().map(Tag::getTag).collect(Collectors.toList()))
                    .link(document.getLink() == null ? null : LinkDto.Response.fromEntity(document.getLink()))
                    .thumbnailImage(
                            document.getImages() != null && !document.getImages().isEmpty() ?
                                    document.getImages().get(0).getImageUrl() : null
                    )
                    .imageCount(document.getImages() == null ? 0 : document.getImages().size())
                    .view(document.getView())
                    .approvalCount(document.getApprovals() == null ?
                            0 : (int) document.getApprovals().stream().filter(Approval::getIsApprove).count()
                    )
                    .rejectCount(document.getApprovals() == null ?
                            0 : (int) document.getApprovals().stream().filter(a -> !a.getIsApprove()).count()
                    )
                    .datetime(DateUtil.convert(document.getCreatedAt()))
                    .build();
        }
    }
}
