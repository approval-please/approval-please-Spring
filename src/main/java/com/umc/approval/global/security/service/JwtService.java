package com.umc.approval.global.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.type.RoleType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static com.umc.approval.global.exception.CustomErrorType.TOKEN_NOT_EXIST;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtService {

    // Expiration Time
    public static final long MINUTE = 1000 * 60;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long MONTH = 30 * DAY;

    public static final long AT_EXP_TIME = 12 * HOUR;
    public static final long RT_EXP_TIME = 3 * MONTH;

    // Refresh Token 갱신 주기(Day)
    public static final long TOKEN_REFRESH_DAYS = 30;

    // Secret
    public static final String JWT_SECRET = "secret_jwt_app_rov_al_p_leaz_secret_key";

    // Header
    public static final String AT_HEADER = "accessToken";
    public static final String RT_HEADER = "refreshToken";
    public static final String TOKEN_HEADER_PREFIX = "Bearer ";

    // Claim
    public static final String CLAIM_ID = "id";
    public static final String CLAIM_ROLE = "role";

    /* 기본적으로 로그인은 필수가 아니지만, 특정 기능에 대해서 로그인 여부 확인이 필요한 경우
     * 로그인이 되어있지 않을 수 있으므로 SecurityContextHolder에 token이 저장되어 있지 않을 수 있음
     * Request Header에서 직접 token을 가져와 userId를 꺼내온다.
     * 로그인 된 경우 : userId 리턴
     * 로그인 안된 경우 : null 리턴
     * ex) 타 사원증 조회 시
     * 타 사원증 조회 시 기본적으로 로그인 하지 않은 사용자도 조회가 가능하다
     * but, 안드 측에서 [팔로우] or [팔로우 취소] 와 같은 ui 처리를 위해서는
     * 해당 사원이 팔로우 되어있는지 여부를 판단하는 Boolean isFollow 가 필요
     * 로그인 안한 경우 - isFollow = false
     * 로그인 한 경우 - isFollow = 팔로우 여부에 따라 true/false
     */
    public Long getIdDirectHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            return null;
        }
        String accessToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        try {
            DecodedJWT decodedJWT = verifyToken(accessToken);
            return decodedJWT.getClaim(CLAIM_ID).asLong();
        } catch (Exception e) {
            return null;
        }
    }

    public String getToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new CustomException(TOKEN_NOT_EXIST);
        }
        return authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
    }

    /* 기본적으로 로그인이 필수인 Endpoint에 대한 요청
     * 로그인이 되어 있으므로 SecurityContextHolder에 access token이 저장되어 있음
     */
    public String getEmail() {
        String accessToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        DecodedJWT decodedJWT = verifyToken(accessToken);
        return decodedJWT.getSubject();
    }

    public String getEmail(String token) {
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getSubject();
    }

    public Long getId() {
        String accessToken = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        DecodedJWT decodedJWT = verifyToken(accessToken);
        return decodedJWT.getClaim(CLAIM_ID).asLong();
    }

    public DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(HMAC256(JWT_SECRET)).build();
        return verifier.verify(token);
    }

    public String createAccessToken(String email, Long id) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + AT_EXP_TIME))
                .withClaim(CLAIM_ROLE, RoleType.USER.getKey())
                .withClaim(CLAIM_ID, id)
                .sign(HMAC256(JWT_SECRET));
    }

    public String createRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withExpiresAt(new Date(System.currentTimeMillis() + RT_EXP_TIME))
                .sign(HMAC256(JWT_SECRET));
    }

    // Refresh Token의 남은 만료 일자(day) 계산
    public Long calculateRefreshExpiredDays(DecodedJWT decodedJWT) {
        long expTime = decodedJWT.getClaim("exp").asLong() * 1000;
        return (expTime - System.currentTimeMillis()) / DAY;
    }
}
