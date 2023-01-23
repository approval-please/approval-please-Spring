package com.umc.approval.domain.report.dto;

import com.umc.approval.domain.document.entity.Document;
import com.umc.approval.domain.like.dto.LikeDto;
import com.umc.approval.domain.like.dto.LikeDto.Response;
import com.umc.approval.domain.like.entity.Like;
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

        public static ReportGetDocumentResponse from(Page<Document> page, List<DocumentListResponse> content) {
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

}
