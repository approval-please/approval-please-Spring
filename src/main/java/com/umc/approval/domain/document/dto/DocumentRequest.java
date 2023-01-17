package com.umc.approval.domain.document.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class DocumentRequest {
    @Data
    public static class PostDocumentRequest{
        @NotNull(message = "게시글의 카테고리는 null일 수 없습니다.")
        private Integer category;

        @NotBlank(message = "게시글의 제목은 null일 수 없습니다.")
        private String title;

        @NotBlank(message = "게시글의 내용은 null일 수 없습니다.")
        private String content;

        private List<String> tag;
        private List<String> linkUrl;
    }
}
