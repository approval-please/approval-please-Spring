package com.umc.approval.domain.document.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    }

    @Builder
    @Data
    public static class DocumentResponse{
        // user
        private String profileImage;
        private String nickname;
        private Integer level;

        // document
        private String category;
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
        private String modifiedAt;
        private Long view;

    }

}
