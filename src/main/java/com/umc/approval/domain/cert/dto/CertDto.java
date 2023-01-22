package com.umc.approval.domain.cert.dto;

import com.umc.approval.domain.cert.entity.Cert;
import com.umc.approval.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class CertDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDto { //전화번호 인증 메시지 전송 Request
        private String toPhoneNumber;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CertSmsRequest{
        private String type;
        private String contentType;
        private String countryCode = "82";
        private String fromPhoneNumber;
        private String content;
        private List<MessageDto> messages;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public class CertSmsResponse{
        private String requestId;
        private LocalDateTime requestTime;
        private String statusCode;
        private String statusName;
    }
}
