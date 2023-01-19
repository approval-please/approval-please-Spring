package com.umc.approval.global.security.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class FirstSnsLoginException extends AuthenticationException {

    private final Long socialId;

    public FirstSnsLoginException(Long socialId) {
        super("");
        this.socialId = socialId;
    }
}
