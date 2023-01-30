package com.umc.approval.global.security.filter;

import com.umc.approval.domain.cert.service.CertService;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.dto.KakaoProfile;
import com.umc.approval.global.security.exception.FirstSnsLoginException;
import com.umc.approval.global.security.exception.NormalAccountExistException;
import com.umc.approval.global.security.exception.OtherSnsAccountExistException;
import com.umc.approval.global.security.service.KakaoOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.umc.approval.global.security.service.JwtService.TOKEN_HEADER_PREFIX;
import static com.umc.approval.global.security.service.KakaoOAuth2Service.KAKAO_SECRET_PASSWORD;
import static com.umc.approval.global.type.SocialType.KAKAO;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class CustomKakaoAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final KakaoOAuth2Service kakaoOAuth2Service;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_HEADER_PREFIX)) {
            throw new AuthenticationCredentialsNotFoundException("AccessToken이 존재하지 않습니다.");
        }
        String accessToken = authorizationHeader.substring(TOKEN_HEADER_PREFIX.length());
        KakaoProfile kakaoProfile = kakaoOAuth2Service.getKakaoProfileWithAccessToken(accessToken);
        String email = (String) kakaoProfile.getKakao_account().get("email");
        User user = userRepository.findByEmail(email).orElse(null);

        // 최초 로그인 (회원가입 response)
        if (user == null) {
            throw new FirstSnsLoginException(kakaoProfile.getId(), email, KAKAO);
        } else if (user.getSocialType() == null) {
            // 일반 계정 존재
            throw new NormalAccountExistException(CertService.maskEmail(user.getEmail()));
        } else if (user.getSocialType() != KAKAO) {
            // 다른 SNS 계정 존재
            throw new OtherSnsAccountExistException(user.getSocialId(), CertService.maskEmail(user.getEmail()), user.getSocialType());
        } else {
            Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), KAKAO_SECRET_PASSWORD);
            return authenticationManager.authenticate(auth);
        }
    }
}
