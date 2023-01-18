package com.umc.approval.domain.toktok.dto;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.stylesheets.LinkStyle;


public class ToktokDto {

    @Data
    public static class PostToktokRequest {

        @NotNull(message = "카테고리는 필수 입력값입니다.")
        @Min(value = 0, message = "카테고리는 0부터 17까지의 정수 값입니다.")
        @Max(value = 17, message = "카테고리는 0부터 17까지의 정수 값입니다.")
        private Integer category;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private String voteTitle;

        private Boolean voteIsSingle;

        private Boolean voteIsAnonymous;

        private Boolean voteIsEnd;

        private List<String> voteOption;

        private List<String> linkUrl;

        private List<String> tag;
    }


}
