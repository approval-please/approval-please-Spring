package com.umc.approval.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CustomErrorType {

    // TODO ErrorCode를 추가해주세요.
    // JWT (1xxxx)
    TOKEN_NOT_EXIST(BAD_REQUEST, 10001, "JWT Token이 존재하지 않습니다."),
    INVALID_TOKEN(BAD_REQUEST, 10002, "유효하지 않은 JWT Token 입니다."),
    ACCESS_TOKEN_EXPIRED(BAD_REQUEST, 10003, "만료된 Access Token 입니다."),
    REFRESH_TOKEN_EXPIRED(BAD_REQUEST, 10004, "만료된 Refresh Token 입니다."),

    // User (2xxxx)
    EMAIL_ALREADY_EXIST(BAD_REQUEST, 20001, "이미 존재하는 이메일입니다."),
    PHONE_NUMBER_ALREADY_EXIST(BAD_REQUEST, 20002, "이미 존재하는 전화번호입니다."),
    LOGIN_FAILED(UNAUTHORIZED, 20003, "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(NOT_FOUND, 20004, "사용자를 찾을 수 없습니다."),
    NO_PERMISSION(FORBIDDEN, 20005, "게시글 수정 및 삭제에 대한 권한이 없습니다."),

    DOCUMENT_NOT_FOUND(NOT_FOUND, 30001, "존재하지 않는 결재서류입니다."),


    // Image (8xxxx)
    IMAGE_UPLOAD_FAILED(BAD_REQUEST, 80001, "이미지 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(BAD_REQUEST, 80002, "이미지 삭제에 실패했습니다."),

    // General (9xxxx)
    INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, 90001, "잘못된 Http Method 요청입니다."),
    INVALID_VALUE(BAD_REQUEST, 90002, "잘못된 입력값입니다."),
    SERVER_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, 90003, "서버 내부에 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String errorMessage;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
