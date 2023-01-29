package com.umc.approval.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.global.exception.ErrorResponse;
import com.umc.approval.global.security.exception.FirstSnsLoginException;
import com.umc.approval.global.security.exception.NormalAccountExistException;
import com.umc.approval.global.security.exception.OtherSnsAccountExistException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.umc.approval.global.exception.CustomErrorType.LOGIN_FAILED;
import static com.umc.approval.global.exception.CustomErrorType.SNS_LOGIN_FAILED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class UserFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        Class<? extends AuthenticationException> eClass = exception.getClass();

        // SNS 로그인 실패
        if (eClass.equals(AuthenticationCredentialsNotFoundException.class)) {
            ErrorResponse errorResponse = new ErrorResponse(SNS_LOGIN_FAILED);
            response.setStatus(SNS_LOGIN_FAILED.getHttpStatus().value());
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        } else if (eClass.equals(FirstSnsLoginException.class)) {
            // SNS 최초 가입
            FirstSnsLoginException castException = (FirstSnsLoginException) exception;
            new ObjectMapper().writeValue(response.getWriter(),
                    new UserDto.SnsTokenResponse(
                            0,
                            castException.getEmail(),
                            castException.getSocialId(),
                            castException.getSocialType(),
                            null, null));
        } else if (eClass.equals(NormalAccountExistException.class)) {
            // SNS 로그인 시 일반 계정 존재
            NormalAccountExistException castException = (NormalAccountExistException) exception;
            new ObjectMapper().writeValue(response.getWriter(),
                    new UserDto.SnsTokenResponse(
                            2,
                            castException.getEmail(),
                            null,
                            null,
                            null, null));
        } else if (eClass.equals(OtherSnsAccountExistException.class)) {
            // SNS 로그인 시 다른 SNS 계정 존재
            OtherSnsAccountExistException castException = (OtherSnsAccountExistException) exception;
            new ObjectMapper().writeValue(response.getWriter(),
                    new UserDto.SnsTokenResponse(
                            2,
                            castException.getEmail(),
                            castException.getSocialId(),
                            castException.getSocialType(),
                            null, null));
        } else {
            // 일반 로그인 실패
            ErrorResponse errorResponse = new ErrorResponse(LOGIN_FAILED);
            response.setStatus(LOGIN_FAILED.getStatusCode());
            new ObjectMapper().writeValue(response.getWriter(), errorResponse);
        }
    }
}
