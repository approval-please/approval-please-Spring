package com.umc.approval.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CustomErrorType {

    // TODO ErrorCode를 추가해주세요.

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
