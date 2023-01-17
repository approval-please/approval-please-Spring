package com.umc.approval.domain.user.dto;

import com.umc.approval.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    private String nickname;
    private String email;
    private String password;
    private String phoneNumber;

    // 일반회원가입 최초 가입자는 level 0, promotionPoint 0L
    public User toEntity() {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .level(0)
                .promotionPoint(0L)
                .build();
    }
}
