package com.umc.approval.domain.user.dto;

import lombok.*;

public class UserDto {

    @Getter
    @AllArgsConstructor
    public static class Request {   //유저 등록 Request
        private String nickname;
        private String email;
        private String password;
        private String phoneNumber;
    }
}
