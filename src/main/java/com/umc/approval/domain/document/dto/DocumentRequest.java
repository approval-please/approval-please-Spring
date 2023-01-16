package com.umc.approval.domain.document.dto;

import lombok.Data;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class DocumentRequest {
    @Data
    public static class PostDocumentRequest{
        private Integer category;
        private String title;
        private String content;
        private List<String> tag;
        private List<String> linkUrl;
    }
}
