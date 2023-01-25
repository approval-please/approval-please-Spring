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

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

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
        public Document toEntity(User user, CategoryType categoryType){
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
    public static class GetDocumentResponse{
        // user
        private String profileImage;
        private String nickname;
        private Integer level;

        // document
        private Integer category;
        private String title;
        private String content;
        private LinkDto.Response link;
        private List<String> tag;
        private List<String> images;

        // state
        private Integer state;
        private Integer approveCount;
        private Integer rejectCount;

        // etc..
        private Integer likedCount;
        private Integer commentCount;
        private String datetime;
        private Long view;


        // Entity -> DTO
        public GetDocumentResponse(Document document, User user, List<String> tagNameList, List<String> imageUrlList,
                                   Link link, int approveCount, int rejectCount, int likedCount, int commentCount) {
            this.profileImage = user.getProfileImage();
            this.nickname = user.getNickname();
            this.level = user.getLevel();

            this.category = document.getCategory().getValue();
            this.title = document.getTitle();
            this.content = document.getContent();
            this.link = link == null ? null : LinkDto.Response.fromEntity(link);
            this.tag = tagNameList;
            this.images = imageUrlList;

            this.state = document.getState();
            this.approveCount = approveCount;
            this.rejectCount = rejectCount;

            this.likedCount = likedCount;
            this.commentCount = commentCount;
            this.datetime = DateUtil.convert(document.getCreatedAt());
            this.view = document.getView();
        }
    }

    @Data
    public static class GetDocumentListResponse {
        private Integer page;
        private Integer totalPage;
        private Long totalElement;
        private List<DocumentDto.DocumentListResponse> content;

        public GetDocumentListResponse(Page<Document> documents, List<DocumentDto.DocumentListResponse> content){
            this.page = documents.getNumber();
            this.totalPage = documents.getTotalPages();
            this.totalElement = documents.getTotalElements();
            this.content = content;
        }
    }

    // 결재서류 목록 조회
    @Data
    public static class DocumentListResponse {
        // document
        private Integer category;
        private String title;
        private String content;
        private List<String> tag;
        private List<String> images;

        // state
        private Integer state;
        private Integer approveCount;
        private Integer rejectCount;

        // etc..
        private String datetime;
        private Long view;

        // Entity -> DTO
        public DocumentListResponse(Document document, List<Tag> tagNameList, List<Image> imageUrlList, List<Approval> approvalList) {
            this.category = document.getCategory().getValue();
            this.title = document.getTitle();
            this.content = document.getContent();
            this.tag = tagNameList.stream().map(Tag::getTag).collect(Collectors.toList());
            this.images = imageUrlList.stream().map(Image::getImageUrl).collect(Collectors.toList());

            this.state = document.getState();
            this.approveCount = (int) approvalList.stream().filter(Approval::getIsApprove).count();
            this.rejectCount = (int) approvalList.stream().filter(a -> !a.getIsApprove()).count();

            this.datetime = DateUtil.convert(document.getCreatedAt());
            this.view = document.getView();
        }
    }

}
