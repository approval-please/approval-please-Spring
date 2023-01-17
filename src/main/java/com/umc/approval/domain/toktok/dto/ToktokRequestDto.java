package com.umc.approval.domain.toktok.dto;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.stylesheets.LinkStyle;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToktokRequestDto {

    @NotNull(message = "카테고리는 필수 입력값입니다.")
    private Integer category;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    private String voteTitle;

    private Boolean isSingle;

    private Boolean isAnonymous;

    private Boolean isEnd;

    private List<String> option;

    private List<String> linkUrl;

    private List<String> tag;

}
