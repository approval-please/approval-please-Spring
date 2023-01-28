package com.umc.approval.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum CustomErrorType {

    // TODO ErrorCode를 추가해주세요.
    // Token (1xxxx)
    TOKEN_NOT_EXIST(BAD_REQUEST, 10001, "JWT Token이 존재하지 않습니다."),
    INVALID_TOKEN(BAD_REQUEST, 10002, "유효하지 않은 JWT Token 입니다."),
    ACCESS_TOKEN_EXPIRED(BAD_REQUEST, 10003, "만료된 Access Token 입니다."),
    REFRESH_TOKEN_EXPIRED(BAD_REQUEST, 10004, "만료된 Refresh Token 입니다."),

    // User (2xxxx)
    EMAIL_ALREADY_EXIST(BAD_REQUEST, 20001, "이미 존재하는 이메일입니다."),
    PHONE_NUMBER_ALREADY_EXIST(BAD_REQUEST, 20002, "이미 존재하는 전화번호입니다."),
    LOGIN_FAILED(UNAUTHORIZED, 20003, "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(NOT_FOUND, 20004, "사용자를 찾을 수 없습니다."),
    NO_PERMISSION(FORBIDDEN, 20005, "수정 및 삭제에 대한 권한이 없습니다."),
    SNS_LOGIN_FAILED(UNAUTHORIZED, 20006, "SNS 로그인에 실패했습니다."),
    CERT_NOT_FOUND(NOT_FOUND, 20007, "전화번호 인증 요청 내역이 존재하지 않습니다."),
    CERT_FAILED(UNAUTHORIZED, 20008, "인증번호가 일치하지 않습니다."),
    CERT_TIME_OVER(REQUEST_TIMEOUT, 20009, "인증 시간이 초과되었습니다."),

    // Document (3xxxx)
    DOCUMENT_NOT_FOUND(NOT_FOUND, 30001, "존재하지 않는 결재서류입니다."),
    CANNOT_APPROVE_MINE(FORBIDDEN, 30002, "자신의 게시글에는 [승인/반려] 선택이 불가합니다."),
    APPROVAL_ALREADY_EXISTS(FORBIDDEN, 30003, "[승인/반려] 선택은 한 번만 가능합니다."),
    CANNOT_APPROVE_OTHER(FORBIDDEN, 30004, "결재서류 최종 [승인/반려] 처리에 대한 권한이 없습니다."),
    ALREADY_APPROVED(FORBIDDEN, 30005, "최종 [승인/반려] 처리된 결재서류입니다."),
    NOT_LIKED_CATEGORY(NOT_FOUND, 30005, "관심 부서로 설정되지 않은 카테고리입니다."),

    // Toktok (4xxxx)
    TOKTOKPOST_NOT_FOUND(NOT_FOUND, 40001, "존재하지 않는 결재톡톡입니다."),
    VOTE_IS_END(BAD_REQUEST, 40002, "투표가 종료된 글은 투표 관련 사항들의 수정이 불가합니다."),

    // Report (5xxxx)
    REPORT_NOT_FOUND(NOT_FOUND, 50001, "존재하지 않는 결재보고서입니다."),
    REPORT_ALREADY_EXISTS(BAD_REQUEST, 50002, "해당 결재서류에 대한 결재보고서가 존재합니다."),

    // Performance (6xxxx)
    PERFORMANCE_NOT_FOUND(NOT_FOUND, 60001, "존재하지 않는 실적입니다."),

    // Comment (7xxxx)
    COMMENT_NOT_FOUND(NOT_FOUND, 70001, "존재하지 않는 댓글입니다."),
    PARENT_COMMENT_NOT_FOUND(NOT_FOUND, 70002, "존재하지 않는 상위 댓글입니다."),
    POST_WITH_COMMENT_NOT_FOUND(NOT_FOUND, 70003, "댓글을 작성한 게시글이 존재하지 않습니다."),

    // Image (8xxxx)
    IMAGE_UPLOAD_FAILED(BAD_REQUEST, 80001, "이미지 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(BAD_REQUEST, 80002, "이미지 삭제에 실패했습니다."),

    // General (9xxxx)
    INVALID_HTTP_METHOD(METHOD_NOT_ALLOWED, 90001, "잘못된 Http Method 요청입니다."),
    INVALID_VALUE(BAD_REQUEST, 90002, "잘못된 입력값입니다."),
    SERVER_INTERNAL_ERROR(INTERNAL_SERVER_ERROR, 90003, "서버 내부에 오류가 발생했습니다."),

    // Follow (10xxxx)
    FOLLOW_NOT_FOUND(NOT_FOUND, 100001,"존재하는 팔로워 사용자가 없습니다."),
    FOLLOWING_NOT_FOUND(NOT_FOUND, 100002, "존재하는 팔로잉 사용자가 없습니다."),

    // Scrap (11xxxx)
    POST_WITH_SCRAP_NOT_FOUND(NOT_FOUND, 110001, "스크랩한 게시글이 존재하지 않습니다."),

    // Vote(12xxxx)
    NOT_MATCH_WITH_VOTE(BAD_REQUEST, 120001, "투표에 해당하는 옵션이 아닙니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String errorMessage;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}
