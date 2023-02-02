package com.umc.approval.domain.notification.dto;

import lombok.*;

public class NotificationDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Request {
        private Long documentId;
        private Long toktokId;
        private Long reportId;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse {
        Boolean isNotification;
    }
}
