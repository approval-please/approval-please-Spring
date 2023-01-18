package com.umc.approval.domain.user.service;

import com.auth0.jwt.interfaces.DecodedJWT;
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
import static com.umc.approval.global.exception.CustomErrorType.INVALID_TOKEN;
import static com.umc.approval.global.exception.CustomErrorType.USER_NOT_FOUND;
import static com.umc.approval.global.security.service.JwtService.TOKEN_REFRESH_DAYS;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void logout() {
        User user = userRepository.findById(jwtService.getId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.deleteRefreshToken();
    }

    public UserDto.TokenResponse refresh(HttpServletRequest request) {
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
        UserDto.TokenResponse tokenResponse = new UserDto.TokenResponse(accessToken, null);

        // Refresh Token 만료시간 계산해 1개월 미만일 시 refresh token도 발급
        long diffDays = jwtService.calculateRefreshExpiredDays(decodedJWT);
        if (diffDays < TOKEN_REFRESH_DAYS) {
            String newRefreshToken = jwtService.createRefreshToken(user.getEmail());
            tokenResponse.setRefreshToken(newRefreshToken);
            user.updateRefreshToken(newRefreshToken);
        }
        return tokenResponse;
    }

    public void resetPassword(UserDto.ResetPasswordRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.encodePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }
}
