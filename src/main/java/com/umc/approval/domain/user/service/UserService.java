package com.umc.approval.domain.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.umc.approval.domain.cert.entity.Cert;
import com.umc.approval.domain.cert.entity.CertRepository;
import com.umc.approval.domain.cert.service.CertService;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.exception.CustomException;
import com.umc.approval.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import static com.umc.approval.global.exception.CustomErrorType.*;
import static com.umc.approval.global.security.service.JwtService.TOKEN_REFRESH_DAYS;
import static com.umc.approval.global.security.service.KakaoOAuth2Service.KAKAO_SECRET_PASSWORD;
import static com.umc.approval.global.type.SocialType.KAKAO;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CertRepository certRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto.EmailCheckResponse emailCheck(UserDto.EmailCheckRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(null);

        if (user == null) {
            return new UserDto.EmailCheckResponse(0, null, null);
        } else if (user.getSocialType() == null) {
            return new UserDto.EmailCheckResponse(1, CertService.maskEmail(user.getEmail()), null);
        } else {
            return new UserDto.EmailCheckResponse(2, CertService.maskEmail(user.getEmail()), user.getSocialType());
        }
    }

    public UserDto.Response signup(UserDto.NormalRequest userCreateNormalRequest) {
        // 전화번호 인증 내역 체크
        certValidation(userCreateNormalRequest.getPhoneNumber());

        // 이메일 중복 체크
        emailDuplicateValidation(userCreateNormalRequest.getEmail());

        // 전화번호 중복 체크
        phoneNumberDuplicateValidation(userCreateNormalRequest.getPhoneNumber());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userCreateNormalRequest.getPassword());

        // 사용자 등록
        User user = userCreateNormalRequest.toEntity(encodedPassword);
        userRepository.save(user);
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        return new UserDto.Response(accessToken, refreshToken);
    }

    public UserDto.Response snsSignup(UserDto.SnsRequest requestDto) {
        // 전화번호 인증 내역 체크
        certValidation(requestDto.getPhoneNumber());

        // 이메일 중복 체크
        emailDuplicateValidation(requestDto.getEmail());

        // 전화번호 중복 체크
        phoneNumberDuplicateValidation(requestDto.getPhoneNumber());

        // 비밀번호 암호화
        String encodedPassword = null;
        if (requestDto.getSocialType() == KAKAO) {
            encodedPassword = passwordEncoder.encode(KAKAO_SECRET_PASSWORD);
        }
        User user = requestDto.toEntity(encodedPassword);
        userRepository.save(user);
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        return new UserDto.Response(accessToken, refreshToken);
    }

    public void logout() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.deleteRefreshToken();
    }

    public UserDto.NormalTokenResponse refresh(HttpServletRequest request) {
        // Refresh Token 유효성 검사
        String refreshToken = jwtService.getToken(request);
        DecodedJWT decodedJWT = jwtService.verifyToken(refreshToken);

        String email = jwtService.getEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(INVALID_TOKEN));

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(INVALID_TOKEN);
        }

        // refresh token 유효성 검사 완료 후 -> access token 재발급
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        UserDto.NormalTokenResponse normalTokenResponse = new UserDto.NormalTokenResponse(accessToken, null);

        // Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급
        long diffDays = jwtService.calculateRefreshExpiredDays(decodedJWT);
        if (diffDays < TOKEN_REFRESH_DAYS) {
            String newRefreshToken = jwtService.createRefreshToken(user.getEmail());
            normalTokenResponse.setRefreshToken(newRefreshToken);
            user.updateRefreshToken(newRefreshToken);
        }
        return normalTokenResponse;
    }

    public void resetPassword(UserDto.ResetPasswordRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.encodePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    private void certValidation(String phoneNumber) {
        Cert cert = certRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException(CERT_NOT_FOUND));
        if(!cert.getIsChecked()) {
            throw new CustomException(CERT_FAILED);
        }
    }

    private void phoneNumberDuplicateValidation(String phoneNumber) {
        userRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(user -> {
                    throw new CustomException(PHONE_NUMBER_ALREADY_EXIST);
                });
    }

    private void emailDuplicateValidation(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new CustomException(EMAIL_ALREADY_EXIST);
                });
    }

    public void checkToken() {
        userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }
}
