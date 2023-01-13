package com.umc.approval.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CustomErrorType {

    // TODO ErrorCode를 추가해주세요.
    // EX)
    // USER_NOT_FOUND(HttpStatus.NOT_FOUND, 10001, "사용자를 찾을 수 없습니다.")

    private final HttpStatus httpStatus;
    private final int code;
    private final String errorMessage;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
