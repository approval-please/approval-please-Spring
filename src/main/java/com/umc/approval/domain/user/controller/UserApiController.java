package com.umc.approval.domain.user.controller;

import com.umc.approval.domain.user.dto.TokenResponseDto;
import com.umc.approval.domain.user.service.UserService;
import com.umc.approval.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.umc.approval.global.exception.CustomErrorType.TOKEN_NOT_EXIST;
import static com.umc.approval.global.security.service.JwtService.TOKEN_HEADER_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(HttpServletRequest request) {
        return ResponseEntity.ok(userService.refresh(request));
    }
}
