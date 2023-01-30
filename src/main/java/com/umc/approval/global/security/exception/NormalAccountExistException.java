package com.umc.approval.global.security.exception;

import com.umc.approval.global.type.SocialType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class NormalAccountExistException extends AuthenticationException {

    private final String email;

    public NormalAccountExistException(String email) {
        super("");
        this.email = email;
    }
}
