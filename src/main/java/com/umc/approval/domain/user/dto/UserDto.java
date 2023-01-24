package com.umc.approval.domain.user.dto;

import com.umc.approval.domain.user.entity.User;
import com.umc.approval.global.type.SocialType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class UserDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailCheckRequest { //이메일 체크 Request
        @NotBlank(message = "이메일은 필수 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;
    }

    @Getter
    @AllArgsConstructor
    public static class EmailCheckResponse {
        private Integer status;
    }

    @Getter
    @AllArgsConstructor
    public static class NormalRequest {   // 일반 유저 등록 Request

        @NotBlank(message = "닉네임은 필수 값입니다.")
        private String nickname;

        @NotBlank(message = "이메일은 필수 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 값입니다.")
        private String password;

        @NotBlank(message = "휴대폰 번호는 필수 값입니다.")
        private String phoneNumber;

        public User toEntity(String encodedPassword) {
            // 일반회원가입 최초 가입자는 level 0, promotionPoint 0L로 초기화
            return User.builder()
                    .nickname(this.nickname)
                    .email(this.email)
                    .password(encodedPassword)
                    .phoneNumber(this.phoneNumber)
                    .level(0)
                    .promotionPoint(0L)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SnsRequest {   // SNS 유저 등록 Request

        @NotBlank(message = "닉네임은 필수 값입니다.")
        private String nickname;

        @NotBlank(message = "이메일은 필수 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "휴대폰 번호는 필수 값입니다.")
        private String phoneNumber;

        private SocialType socialType;
        private Long socialId;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .nickname(this.nickname)
                    .email(this.getEmail())
                    .password(encodedPassword)
                    .phoneNumber(this.getPhoneNumber())
                    .socialType(this.socialType)
                    .socialId(this.socialId)
                    .level(0)
                    .promotionPoint(0L)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class NormalTokenResponse {
        private String accessToken;
        private String refreshToken;

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SnsTokenResponse {
        private Boolean isFirst;
        private Long socialId;
        private SocialType socialType;
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    public static class ResetPasswordRequest {

        @NotBlank(message = "이메일은 필수 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 값입니다.")
        private String newPassword;
    }

    // 사원증 프로필 수정
    @Data
    public static class ProfileRequest {
        private String nickname;
        private String introduction;
        private String image;
    }
}
