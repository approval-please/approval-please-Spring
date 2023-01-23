package com.umc.approval.domain.document.dto;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.type.CategoryType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.datatype.DatatypeConstants;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

        private List<String> tag;
        private String linkUrl;

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
                    .linkUrl(linkUrl)
                    .build();
        }
    }

    @Data
    public static class DocumentResponse{
        // user
        private String profileImage;
        private String nickname;
        private Integer level;

        // document
        private Integer category;
        private String title;
        private String content;
        private String linkUrl;
        private List<String> tag;
        private List<String> images;

        // state
        private Integer state;
        private Integer approveCount;
        private Integer rejectCount;

        // etc..
        private Integer likedCount;
        private Integer commentCount;
        private String createdAt;
        private Long view;


        // Entity -> DTO
        public DocumentResponse(Document document, User user, List<String> tagNameList, List<String> imageUrlList,
                int approveCount, int rejectCount, int likedCount, int commentCount) {
            this.profileImage = user.getProfileImage();
            this.nickname = user.getNickname();
            this.level = user.getLevel();

            this.category = document.getCategory().getValue();
            this.title = document.getTitle();
            this.content = document.getContent();
            this.linkUrl = document.getLinkUrl();
            this.tag = tagNameList;
            this.images = imageUrlList;

            this.state = document.getState();
            this.approveCount = approveCount;
            this.rejectCount = rejectCount;

            this.likedCount = likedCount;
            this.commentCount = commentCount;
            this.createdAt = document.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
            this.view = document.getView();
        }
    }

}
