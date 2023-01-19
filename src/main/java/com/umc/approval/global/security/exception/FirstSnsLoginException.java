package com.umc.approval.global.security.exception;

import com.umc.approval.global.type.SocialType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class FirstSnsLoginException extends AuthenticationException {

    private final Long socialId;
    private final SocialType socialType;

    public FirstSnsLoginException(Long socialId, SocialType socialType) {
        super("");
        this.socialId = socialId;
        this.socialType = socialType;
    }
}
