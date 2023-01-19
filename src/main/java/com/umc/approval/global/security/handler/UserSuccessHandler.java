package com.umc.approval.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.domain.user.dto.UserDto;
import com.umc.approval.domain.user.entity.User;
import com.umc.approval.domain.user.entity.UserRepository;
import com.umc.approval.global.security.service.CustomUserDetails;
import com.umc.approval.global.security.service.JwtService;
import com.umc.approval.global.type.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Component
public class UserSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        SocialType socialType = (SocialType) authentication.getCredentials();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("가입된 이메일이 존재하지 않습니다."));

        // JWT Token 생성 & Response
        String accessToken = jwtService.createAccessToken(user.getEmail(), user.getId());
        String refreshToken = jwtService.createRefreshToken(user.getEmail());
        user.updateRefreshToken(refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        // SNS 유저 로그인 response
        if (socialType != null) {
            new ObjectMapper().writeValue(response.getWriter(),
                    new UserDto.SnsTokenResponse(false, user.getSocialId(), accessToken, refreshToken));
        } else {
            // 일반 유저 로그인 response
            new ObjectMapper().writeValue(response.getWriter(),
                    new UserDto.NormalTokenResponse(accessToken, refreshToken));
        }
    }
}
