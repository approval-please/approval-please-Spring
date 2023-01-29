package com.umc.approval.global.security.exception;

import com.umc.approval.global.type.SocialType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OtherSnsAccountExistException extends AuthenticationException {

    private final Long socialId;
    private final String email;
    private final SocialType socialType;

    public OtherSnsAccountExistException(Long socialId, String email, SocialType socialType) {
        super("");
        this.socialId = socialId;
        this.email = email;
        this.socialType = socialType;
    }
}
