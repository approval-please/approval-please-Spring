package com.umc.approval.domain.link.dto;

import com.umc.approval.domain.link.entity.Link;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class LinkDto {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        @NotBlank(message = "링크의 url은 필수 값입니다.")
        private String url;

        private String title;
        private String image;
    }

    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Response {
        private String url;
        private String title;
        private String image;

        public static Response fromEntity(Link link) {
            return Response.builder()
                    .url(link.getUrl())
                    .title(link.getTitle())
                    .image(link.getImage())
                    .build();
        }
    }
}
