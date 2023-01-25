package com.umc.approval.domain.link.dto;

import com.umc.approval.domain.link.entity.Link;
import lombok.*;

public class LinkDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Request {
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
