package com.umc.approval.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;

import static com.umc.approval.global.exception.CustomErrorType.*;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    // CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("[CustomException] url: {} | errorType: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), e.getErrorType(), e.getMessage(), e.getCause());

        return ResponseEntity
                .status(e.getErrorType().getHttpStatus())
                .body(new ErrorResponse(e));
    }

    // Not Support Http Method Exception
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMethodException(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request
    ) {
        log.error("[HttpRequestMethodNotSupportedException] " +
                        "url: {} | errorType: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), INVALID_HTTP_METHOD, INVALID_HTTP_METHOD.getErrorMessage(), e);

        return ResponseEntity
                .status(INVALID_HTTP_METHOD.getHttpStatus())
                .body(new ErrorResponse(INVALID_HTTP_METHOD));
    }

    // Validation Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String validationMessage = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        log.error("[MethodArgumentNotValidException] url: {} | errorType: {} | errorMessage: {} | cause Exception: ",
                request.getRequestURL(), INVALID_VALUE, validationMessage, e);

        CustomException customException = new CustomException(INVALID_VALUE, validationMessage);
        return ResponseEntity
                .status(INVALID_VALUE.getHttpStatus())
                .body(new ErrorResponse(customException));
    }

    // 이외 Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e, HttpServletRequest request) {
        log.error("[Common Exception] url: {} | errorMessage: {}",
                request.getRequestURL(), e.getMessage());
        return ResponseEntity
                .status(SERVER_INTERNAL_ERROR.getHttpStatus())
                .body(new ErrorResponse(SERVER_INTERNAL_ERROR));
    }
}
