package com.umc.approval.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.global.exception.CustomErrorType;
import com.umc.approval.global.exception.ErrorResponse;
import com.umc.approval.global.security.exception.FirstSnsLoginException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.umc.approval.global.exception.CustomErrorType.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class UserFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        Class<? extends AuthenticationException> eClass = exception.getClass();

        // SNS 로그인 실패
        if (eClass.equals(AuthenticationCredentialsNotFoundException.class)) {
            ErrorResponse errorResponse = new ErrorResponse(SNS_LOGIN_FAILED);
            response.setStatus(SNS_LOGIN_FAILED.getHttpStatus().value());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        } else if (eClass.equals(FirstSnsLoginException.class)) {
            // SNS 최초 가입
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            Long socialId = ((FirstSnsLoginException) exception).getSocialId();
            new ObjectMapper().writeValue(response.getWriter(),
                    new UserDto.SnsTokenResponse(true, socialId, null, null));
        } else {
            // 일반 로그인 실패
            ErrorResponse errorResponse = new ErrorResponse(LOGIN_FAILED);
            response.setStatus(LOGIN_FAILED.getStatusCode());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("utf-8");
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
    }
}
