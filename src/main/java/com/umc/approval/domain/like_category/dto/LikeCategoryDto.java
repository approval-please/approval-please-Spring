package com.umc.approval.domain.like_category.dto;

import lombok.*;

import java.util.List;

public class LikeCategoryDto {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        private List<Integer> likedCategory;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Boolean isSet;
        private List<Integer> likedCategory;
    }
}
