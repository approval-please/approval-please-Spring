package com.umc.approval.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserDto {

    @Getter
    @AllArgsConstructor
    public static class Request {   //유저 등록 Request
        private String nickname;
        private String email;
        private String password;
        private String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ResetPasswordRequest {
        private String email;
        private String newPassword;
    }
}
