package com.umc.approval.domain.cert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class CertDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertRequest { //전화번호 인증 Request
        @NotBlank(message = "전화번호를 입력해 주세요.")
        private String phoneNumber;
    }
}
