package com.umc.approval.domain.user.dto;

import com.umc.approval.domain.user.entity.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static lombok.AccessLevel.PROTECTED;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = PROTECTED)
    @AllArgsConstructor(access= AccessLevel.PRIVATE)
    public static class Request {   //유저 등록 Request
        private String nickname;
        private String email;
        private String password;
        private String phoneNumber;
    }
}
