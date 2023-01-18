package com.umc.approval.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;


public class UserDto {

    @Getter
    @AllArgsConstructor
    public static class TokenResponseDto {
        private String accessToken;
        private String refreshToken;

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
