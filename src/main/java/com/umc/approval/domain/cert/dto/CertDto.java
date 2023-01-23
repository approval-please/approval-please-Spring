package com.umc.approval.domain.cert.dto;

import com.umc.approval.domain.cert.entity.Cert;
import com.umc.approval.global.type.SocialType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CertDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertRequest {
        private String phoneNumber;
        private String certNumber;

        public Cert toEntity() {
            return Cert.builder()
                    .phoneNumber(this.phoneNumber)
                    .certNumber(this.certNumber)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneRequest {  // 전화번호 인증 Request
        private String phoneNumber;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertCheckResponse {
        private Boolean isDuplicate;
        private String email;
        private SocialType socialType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto { // 메시지 Dto
        private String to;
        // private String subject;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SmsRequest{ // 문자 발송 request
        private String type;
        private String contentType;
        private String countryCode;
        private String from;
        private String content;
        private List<MessageDto> messages;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SmsResponse{ // 문자 발송 response
        private String requestId;
        private LocalDateTime requestTime;
        private String statusCode;
        private String statusName;
    }
}
