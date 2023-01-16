package com.umc.approval.domain.document.dto;

import lombok.Data;

import java.util.List;

public class DocumentRequest {
    @Data
    public static class BasicWorkRequest{
        private Integer category;
        private String title;
        private String content;
        private List<String> tag;
        private List<String> linkUrl;
    }
}
