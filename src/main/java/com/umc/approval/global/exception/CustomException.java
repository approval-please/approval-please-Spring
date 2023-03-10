package com.umc.approval.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final CustomErrorType errorType;
    private final int code;
    private final String errorMessage;

    // Without Cause Exception
    public CustomException(CustomErrorType errorType) {
        super(errorType.getErrorMessage());
        this.errorType = errorType;
        this.code = errorType.getCode();
        this.errorMessage = errorType.getErrorMessage();
    }

    public CustomException(CustomErrorType errorType, String errorMessage) {
        super(errorMessage);
        this.errorType = errorType;
        this.code = errorType.getCode();
        this.errorMessage = errorMessage;
    }

    // With Cause Exception
    public CustomException(CustomErrorType errorType, Exception cause) {
        super(errorType.getErrorMessage(), cause);
        this.errorType = errorType;
        this.code = errorType.getCode();
        this.errorMessage = errorType.getErrorMessage();
    }

    public CustomException(CustomErrorType errorType, String errorMessage, Exception cause) {
        super(errorMessage, cause);
        this.errorType = errorType;
        this.code = errorType.getCode();
        this.errorMessage = errorMessage;
    }
}
