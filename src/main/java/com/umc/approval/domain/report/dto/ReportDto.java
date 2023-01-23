package com.umc.approval.domain.report.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

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

}
